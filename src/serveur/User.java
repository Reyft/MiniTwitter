package serveur;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class User extends UnicastRemoteObject implements Serializable, UserInterface {
	private String name;
	private String password;
	private List<String> abo;

	public User() throws RemoteException{
		this("anonymous", "admin");
	}

	public User(String name, String password) throws RemoteException{
		super();
		this.name = name;
		this.password = password;
		abo = new ArrayList<String>();
	}
	
	@Override
	public String getPassword(){
		return password;
	}
	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<String> getAbo(){
		return abo;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void addAbo(String name) throws RemoteException {
		abo.add(name);
	}	
}
