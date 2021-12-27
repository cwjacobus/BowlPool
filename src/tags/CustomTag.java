package tags;

import java.util.List;

public class CustomTag {

	public static boolean notIn(List<Object> list, Object o) {
		boolean found = o != null ? !list.contains(o) : false;
	    return found;
	}

}
