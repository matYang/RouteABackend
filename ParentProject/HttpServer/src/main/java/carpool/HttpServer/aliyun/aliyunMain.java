package carpool.HttpServer.aliyun;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.CannedAccessControlList;
import com.aliyun.openservices.oss.model.GetObjectRequest;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;

import carpool.HttpServer.aws.AwsMain;
import carpool.HttpServer.aws.IdleConnectionReaper;
import carpool.HttpServer.carpoolDAO.CarpoolDaoBasic;
import carpool.HttpServer.common.DateUtility;
import carpool.HttpServer.common.DebugLog;
import carpool.HttpServer.configurations.DatabaseConfig;
import carpool.HttpServer.configurations.ServerConfig;
import carpool.HttpServer.model.representation.SearchRepresentation;


public class aliyunMain {
	
	private static final String myAccessKeyID = ServerConfig.AliyunAccessKeyID;
	private static final String mySecretKey = ServerConfig.AliyunAccessKeySecrete;
	private static final String ProfileBucket = ServerConfig.AliyunProfileBucket;	
	

	static Logger logger = Logger.getLogger(aliyunMain.class);

	public static void createUserFile(int userId){

		OSSClient client = new OSSClient(myAccessKeyID, mySecretKey);
		File file = new File(getUserSHLocalFileName(userId));
		
		try{
			file.createNewFile();
			InputStream content = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(file.length());
			client.putObject(ProfileBucket, getUserSHFileName(userId), content, meta);
		} catch(ClientException | OSSException e){
			e.printStackTrace();  
			DebugLog.d(e);
		} catch(IOException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally{
			file.delete();
			IdleConnectionReaper.shutdown();
		}

	}



	public static boolean migrateUserSearchHistory(int userId){
		OSSClient client = new OSSClient(myAccessKeyID, mySecretKey);
		Jedis redis = CarpoolDaoBasic.getJedis();

		//Clean all the usrSRH
		OSSObject object = null; 
		File file = new File(getUserSHLocalFileName(userId));

		try{
			//initialize the new empty file
			file.createNewFile();
			object = client.getObject(ProfileBucket, getUserSHFileName(userId));
			writeS3ToFile(object, file);			

			//Get redis SRH of each user
			String rediskey = getUserSHRedisKey(userId);
			long upper = redis.llen(rediskey);
			List<String> appendString = redis.lrange(rediskey, 0, upper-1);

			//Write to file			
			writeSHToFile(appendString, file);	
			InputStream content = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(file.length());
			client.putObject(ProfileBucket, getUserSHFileName(userId), content, meta);			 

			//clean redis
			redis.del(rediskey);
			
		} catch(ClientException | OSSException e){
			e.printStackTrace();  
			DebugLog.d(e);
		} catch(IOException e){
			e.printStackTrace();
			DebugLog.d(e);
		} finally{			
			CarpoolDaoBasic.returnJedis(redis);
			file.delete();
			IdleConnectionReaper.shutdown();
		}
		
		return true;
	}

	public static boolean migrateAlltheUsersSearchHistory(){
		Jedis redis = CarpoolDaoBasic.getJedis();
		Set<String> keyset = redis.keys(DatabaseConfig.redisSearchHistoryPrefix + "*");
		CarpoolDaoBasic.returnJedis(redis);
		
		for (String key : keyset){			
			int userId = Integer.parseInt(key.substring(DatabaseConfig.redisSearchHistoryPrefix.length()));
			migrateUserSearchHistory(userId);
		}

		return true;
	} 
	
	
	public static  ArrayList<SearchRepresentation> getUserSearchHistory(int userId){
		ArrayList<SearchRepresentation> list = new ArrayList<SearchRepresentation>();
		File file = new File(getUserSHLocalFileName(userId));
		
		OSSClient client = new OSSClient(myAccessKeyID, mySecretKey);
		OSSObject object = null;			

		try{
			file.createNewFile();
			
			object = client.getObject(new GetObjectRequest(ProfileBucket, getUserSHFileName(userId)));
			writeS3ToFile(object, file);
			
			//read
			list.addAll(readSHFromFile(file));
			list.addAll(getUserSearchHistoryFromRedis(userId));
					
		} catch(OSSException e){			
			if(e.getErrorCode().equals("NoSuchKey")){
				list.addAll(getUserSearchHistoryFromRedis(userId));
			}
			else{
				DebugLog.d(e);
			}
		} catch(IOException e){
			DebugLog.d(e);
		} finally{			
			file.delete();	
			IdleConnectionReaper.shutdown();
		}
		return list;
	}


	public static void storeSearchHistory(SearchRepresentation sr,int userId){

		String rediskey = getUserSHRedisKey(userId);
		Jedis redis = CarpoolDaoBasic.getJedis();
		redis.lpush(rediskey, sr.toSerializedString());
		
		OSSObject object = null;		
		//check
		if(redis.llen(rediskey) >= DatabaseConfig.redisSearchHistoryUpbound){
			OSSClient client = new OSSClient(myAccessKeyID, mySecretKey);
			List<String> appendString = redis.lrange(rediskey, 0, DatabaseConfig.redisSearchHistoryUpbound-1);
			String fileName = getUserSHFileName(userId);
			File file = new File(getUserSHLocalFileName(userId));

			try{
				file.createNewFile();				
				object = client.getObject(new GetObjectRequest(ProfileBucket,fileName));
				writeS3ToFile(object, file);
				
				writeSHToFile(appendString, file);
				InputStream content = new FileInputStream(file);
				ObjectMetadata meta = new ObjectMetadata();
				meta.setContentLength(file.length());
				client.putObject(ProfileBucket, fileName, content, meta);
				
				//clean redis
				redis.del(rediskey);
			} catch(OSSException e){	
				if(e.getErrorCode().equals("NoSuchKey")){
					try{
						writeSHToFile(appendString, file);
						InputStream content = new FileInputStream(file);
						ObjectMetadata meta = new ObjectMetadata();
						meta.setContentLength(file.length());
						client.putObject(ProfileBucket, fileName, content, meta); 
						
						//clean redis
						redis.del(rediskey);
					} catch (IOException e1){
						DebugLog.d(e1);
					}
				}
				else{
					DebugLog.d(e);
				}
			} catch (IOException e){
				DebugLog.d(e);
			} finally {				
				CarpoolDaoBasic.returnJedis(redis);				
				file.delete();
				IdleConnectionReaper.shutdown();	
			}
		} else{
			CarpoolDaoBasic.returnJedis(redis);
		}
	}		
	
	public static String uploadImg(int userId, File file, String imgName, String Bucket){
		return uploadImg(userId, file, imgName, Bucket,true);
	}
	
	
	//the boolean shouldDelete is used for testing so that the sample is not deleted every time
	public static String uploadImg(int userId, File file, String imgName, String Bucket,boolean shouldDelete){

		OSSClient client = new OSSClient(myAccessKeyID, mySecretKey);		
		URL s = null;		
		try{
			String tempImageKey = getUserImageKey(userId, imgName);
			InputStream content = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(file.length());
			client.putObject(ProfileBucket, tempImageKey, content, meta);
//			s3Client.putObject(new PutObjectRequest(Bucket,tempImageKey,file).withCannedAcl(CannedAccessControlList.PublicRead));
			
		} catch(ClientException | OSSException e){
			e.printStackTrace();  
			DebugLog.d(e);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			DebugLog.d(e);
		} finally{
			IdleConnectionReaper.shutdown();
			if (shouldDelete){
				file.delete();
			}
		}		
		return  s == null ? null : s.toString();	

	}

	private static ArrayList<SearchRepresentation> getUserSearchHistoryFromRedis(int userId){
		Jedis redis = CarpoolDaoBasic.getJedis();
		List<String> appendString = redis.lrange(getUserSHRedisKey(userId), 0, DatabaseConfig.redisSearchHistoryUpbound-1);
		CarpoolDaoBasic.returnJedis(redis);
		
		ArrayList<SearchRepresentation> srList = new ArrayList<SearchRepresentation>();
		for (String str : appendString){
			srList.add(new SearchRepresentation(str));
		}
		
		return srList;
	}
	
	private static String getUserSHRedisKey(int userId){
		return DatabaseConfig.redisSearchHistoryPrefix+userId;
	}
	
	private static String getUserSHFileName(int userId){
		return userId + "/" + userId + "_sr.txt";
	}
	
	private static String getUserSHLocalFileName(int userId){
		return ServerConfig.pathToSearchHistoryFolder + userId + ServerConfig.searchHistoryFileSufix;	
	}
	
	private static String getUserImageKey(int userId, String imageName){
		long msec = DateUtility.getCurTime();		
		return userId + "/" + imageName + "-" + msec + ".png";
	}
	
	private static void writeSHToFile(List<String> shList, File file) throws IOException{
		BufferedWriter	bw = new BufferedWriter(new FileWriter(file, true));
		for(int i = shList.size() - 1; i >= 0; i--){
			bw.write(shList.get(i));   
			bw.newLine();
		}    
		bw.flush();
		bw.close();
	}
	
	private static void writeS3ToFile(OSSObject object, File file) throws IOException{
		InputStream objectData = object.getObjectContent(); 
		InputStream reader = new BufferedInputStream(objectData);      
		OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		
		int read = -1;
		while ( ( read = reader.read() ) != -1 ) {       
			writer.write(read);
		}
		writer.flush();
		writer.close();
		reader.close();
		objectData.close(); 
	}
	
	private static List<SearchRepresentation> readSHFromFile(File file) throws IOException{
		ArrayList<SearchRepresentation> shList = new ArrayList<SearchRepresentation>();
		BufferedReader bfreader = new BufferedReader(new FileReader(file));
		String line = bfreader.readLine();
		while(line!=null){
			shList.add(new SearchRepresentation(line));
			line = bfreader.readLine();
		}
		bfreader.close();
		return shList;
	}
}
