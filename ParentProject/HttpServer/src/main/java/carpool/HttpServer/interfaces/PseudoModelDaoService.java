package carpool.HttpServer.interfaces;

import java.util.ArrayList;

import carpool.HttpServer.exception.PseudoException;

public interface PseudoModelDaoService {
	
	
	public ArrayList<PseudoModel> getAll();
	
	public PseudoModel getById() throws PseudoException;
	
	public PseudoModel create(PseudoModel c) throws PseudoException;
	
	public PseudoModel update(PseudoModel u) throws PseudoException;
	
	public PseudoModel deleteById(int id) throws PseudoException;
	
	public void deleteAll();
	
}
