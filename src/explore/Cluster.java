package explore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import analysis.language.actor.GenericClass;
import analysis.language.actor.GenericDefinition;

public class Cluster {

//---  Instance Variables   -------------------------------------------------------------------
	
	private String[] address;
	private ArrayList<Cluster> children;
	private HashSet<GenericDefinition> composite;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Cluster(String[] givenAddress) {
		address = givenAddress;
		children = new ArrayList<Cluster>();
		composite = new HashSet<GenericDefinition>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addComponent(String[] path, GenericDefinition in) {
		System.out.println(in.getName());
		if(path == null || path.length == 0) {
			composite.add(in);
		}
		else {
			Cluster use = findChild(path[0]);
			if(use == null) {
				addCluster(path);
				use = findChild(path[0]);
			}
			use.addComponent(tearArray(path), in);
		}
	}
	
	public void addCluster(String[] path) {
		if(path == null || path.length == 0) {
			return;
		}
		Cluster next = findChild(path[0]);
		if(next == null) {
			next = new Cluster(mergePath(path));
			children.add(next);
		}
		next.addCluster(tearArray(path));
	}
	
	private Cluster findChild(String step) {
		for(Cluster c : children) {
			if(step.equals(c.getTernary())) {
				return c;
			}
		}
		return null;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Cluster getCluster(String[] path) {
		if(path == null || path.length == 0) {
			return this;
		}
		Cluster next = findChild(path[0]);
		if(next == null) {
			return null;
		}
		return next.getCluster(tearArray(path));
	}
	
	public ArrayList<Cluster> getChildren(){
		return children;
	}
	
	public HashSet<GenericDefinition> getComponents(){
		return composite;
	}
	
	public String[] getAddressArray() {
		return address;
	}
	
	public String getAddress() {
		if(address == null || address.length == 0) {
			return "";
		}
		String out = address[0];
		for(int i = 1; i < address.length; i++)
			out += "." + address[i];
		return out;
	}
	
	private String getTernary() {
		return address[address.length - 1];
	}

//---  Mechanics   ----------------------------------------------------------------------------
	
	private String[] mergePath(String[] tack) {
		String[] out = new String[address.length + tack.length];
		for(int i = 0; i < address.length; i++)
			out[i] = address[i];
		for(int i = address.length; i < out.length; i++)
			out[i] = tack[i - address.length];
		return out;
	}
	
	private String[] tearArray(String[] in) {
		return Arrays.copyOfRange(in, 1, in.length - 1);
	}

	public void debugPrintOut() {
		debugPrintOut(0);
	}
	
	private void debugPrintOut(int d) {
		System.out.println(tabBuffer(d) + getAddress());
		for(GenericDefinition gd : getComponents()) {
			System.out.println(tabBuffer(d) + gd.getName());
		}
		for(Cluster c : getChildren()) {
			c.debugPrintOut(d + 1);
		}
	}
	
	private String tabBuffer(int d) {
		String out = "";
		for(int i = 0; i < d; i++) {
			out += "\t";
		}
		return out;
	}
	
}
