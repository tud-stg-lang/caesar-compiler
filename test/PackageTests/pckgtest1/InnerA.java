cclass generated.pckgtest1.OuterA;

import java.util.LinkedList;
import java.util.List;

public cclass InnerA {
	private List list = new LinkedList(); 
	private HashMap map = new HashMap();
	
	public void m() {
		list.add("aaa");
		map.put("aaa", "bbb");
	}
	
	public List getList() {
		return list;
	}
}