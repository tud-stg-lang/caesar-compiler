cclass generated.pckgtest6.OuterA;

import java.util.ArrayList;
import java.util.List;

public cclass InnerB {
	private List list = new ArrayList(); 
	private HashMap map = new HashMap();
	
	public void m() {
		list.add("aaa");
		map.put("aaa", "bbb");
	}
	
	public List getList() {
		return list;
	}
}