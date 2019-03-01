package com.sdk.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sdk.db.entity.AvatarHistory;
import com.sdk.db.entity.EaseMessageHistoryList;
import com.sdk.db.entity.NotifyMessage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author:zlcai
 * @new date:2016-11-23
 * @last date:2016-11-23
 * @remark:
 **/

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "FDR";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
//		createTable(db, CacheData.class);
//		createTable(db, ShareTrendsTask.class);
		createTable(db, EaseMessageHistoryList.class);
		createTable(db, NotifyMessage.class);
		createTable(db, AvatarHistory.class);
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
        dropTable(db, EaseMessageHistoryList.class);
        dropTable(db, NotifyMessage.class);
        dropTable(db, AvatarHistory.class);
        onCreate(db);

	}

    public void dropTable(SQLiteDatabase db, Class clazz){
        StringBuffer sb = new StringBuffer();
        sb.append("DROP TABLE IF EXISTS ");
        sb.append(clazz.getSimpleName().toLowerCase());//使用类名作为表名
        sb.append("(");
        db.execSQL(sb.toString());
    }
	
	@SuppressLint("DefaultLocale")
	public void createTable(SQLiteDatabase db, Class clazz) {  
		
        StringBuffer sb = new StringBuffer();  
        sb.append("create table if not exists ");  
        sb.append(clazz.getSimpleName().toLowerCase());//使用类名作为表名  
        sb.append("(");  
  
        List<Field> fields = getAllField(clazz);  
        for (Field field : fields) {  
            int modify = field.getModifiers();  
            //过滤所有的静态字段和常量  
            if (!Modifier.isStatic(modify) && !Modifier.isFinal(modify)) {  
                //获取注解，如果该字段上有@Column(saveable=false)的注解，则不保存  
                Column annotation = field.getAnnotation(Column.class);  
                // field.getDeclaredAnnotations()  
                if (annotation == null || annotation.saveable()) {  
                    Class<?> fieldType = field.getType();  
                      
                    sb.append(field.getName());  
                    sb.append(" ");  
                    String fieldTypeName = fieldType.getName();  
                    if (fieldTypeName == null) {  
                        fieldTypeName = "text";  
                    }
                    sb.append(getFieldType(fieldTypeName));  
                    if(field.getName().equals("id")){
                    	sb.append(" primary key autoincrement");
                    }
                    sb.append(",");
                }  
            }  
        }  
        //去掉最后一个“,”号  
        sb.deleteCharAt(sb.length() - 1);  
        sb.append(")");  
               
        //创建数据库
        try{
        	db.execSQL(sb.toString());  
        }catch(SQLException e){
        	Log.e("", e.toString());
        }
        
        Log.e("创建数据库：", sb.toString());
    }
	
	/**获取clazz类的所有字段，包括父类*/  
    public List<Field> getAllField(@SuppressWarnings("rawtypes") Class clazz) {  
        List<Field> fields = new ArrayList<Field>();  
  
        //如果是基础类行或是Object类型则直接返回  
        if (isBasicType(clazz) || clazz == Object.class) {  
            return null;  
        } else {  
            //获取自身的所有字段  
            List<Field> asList = Arrays.asList(clazz.getDeclaredFields());  
            fields.addAll(asList);  
            //获取父类的字段  
            List<Field> allField = getAllField(clazz.getSuperclass());  
            if (allField != null) {  
                fields.addAll(allField);  
            }
            return fields;  
        }  
    }
    
    /**获取字段的类型映射的数据库类型*/  
    public String getFieldType(String fieldType) {  
        if (fieldType != null) {  
	        if (fieldType.equals("int") || fieldType.equals("java.lang.Integer")) {  
	            return "integer";  
	        }  
	        if (fieldType.equals("long") || fieldType.equals("java.lang.Long")) {  
	            return "integer";  
	        }  
	        if (fieldType.equals("short") || fieldType.equals("java.lang.Short")) {  
	            return "integer";  
	        }  
	        if (fieldType.equals("boolean") || fieldType.equals("java.lang.Boolean")) {  
	            return "integer";  
	            }
	  
	            if (fieldType.equals("java.util.Date")) {  
	            return "integer";  
	            }
	  
	            if (fieldType.equals("float") || fieldType.equals("java.lang.Float")) {  
	            return "real";  
	        }  
	        if (fieldType.equals("double") || fieldType.equals("java.lang.Double")) {  
	            return "real";  
	            }  
	  
	  
	            if (fieldType.equals("char") || fieldType.equals("java.lang.Character")) {  
	            return "text";  
	        }  
	        if (fieldType.equals("java.lang.String")) {  
	            return "text";  
	        }  
        }  
        return null;  
    }
    
    /** 
     * 判断类型clazz是否是基本类型 
     *  
     * @param clazz 
     * @return 
     */  
     public static boolean isBasicType(Class<? extends Object> clazz) {  
      
        if (clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Double.class)  
        		|| clazz.equals(Boolean.class) || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(String.class)) {  
            return true;  
        } else {  
            return false;  
        } 
     }
     
     @Retention(RetentionPolicy.RUNTIME)  
     @Target(ElementType.FIELD)  
     public @interface Column {  
         boolean saveable() default true;  
     }
}
