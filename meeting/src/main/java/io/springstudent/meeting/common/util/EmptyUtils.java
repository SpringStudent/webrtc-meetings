package io.springstudent.meeting.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class EmptyUtils {

	public static final String EMPTY_STR="";

	public static boolean isBlank(String source){
		return source==null || source.trim().length()==0;
	}
	public static boolean isNotBlank(String source){
		return !isBlank(source);
	}
	public static boolean isEmpty(String source){
		return source==null || source.length()==0;
	}
	public static boolean isNotEmpty(String source){
		return !isEmpty(source);
	}
	public static <T> boolean isEmpty(Collection<T> list){
		return list==null || list.size()==0;
	}
	public static <T> boolean isNotEmpty(Collection<T> list){
		return !isEmpty(list);
	}
	public static <T,K> boolean isEmpty(Map<T,K> map){
		return map==null || map.size()==0;
	}
	public static <T,K> boolean isNotEmpty(Map<T,K> map){
		return !isEmpty(map);
	}
	public static <T> boolean isEmpty(T[] arrays){
		return arrays==null || arrays.length==0;
	}
	public static <T> boolean isNotEmpty(T[] arrays){
		return !isEmpty(arrays);
	}
	public static boolean isEmpty(Supplier supplier){
		Object object = supplier.get();
		if(object == null){
			return true;
		}else if(object instanceof String){
			return isEmpty((String) object);
		}else if(object instanceof Collection){
			return isEmpty((Collection) object);
		}else if (object instanceof Map){
			return isEmpty((Map) object);
		}else{
			return false;
		}
	}
	public static boolean isNotEmpty(Supplier supplier){
		Object object = supplier.get();
		if(object == null){
			return false;
		}else if(object instanceof String){
			return isNotEmpty((String) object);
		}else if(object instanceof Collection){
			return isNotEmpty((Collection) object);
		}else if (object instanceof Map){
			return isNotEmpty((Map) object);
		}else{
			return true;
		}
	}
}
