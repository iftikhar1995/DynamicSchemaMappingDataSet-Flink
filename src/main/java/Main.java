import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.DataSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;


/**
 * The class will act as entry point. This class will apply the schema on the specified data file
 */
public class Main {

    static List<String> fields_name = new ArrayList<String>();
    static List<Class<?>> fields_type = new ArrayList<Class<?>>();
    static String soucePath = "";
    static String pojoName = "";
    static String configFilePath = "src/main/resources/config.json";

    public static void main(String [] args) throws Exception {

        //Step - 1 :: parsing the configuration file
        parseJSON();

        //Step-2 :: Getting the Execution Environment
        final ExecutionEnvironment ENV = ExecutionEnvironment.getExecutionEnvironment();

        //Step-3 :: Creating the dynamic class
        final Class<?> clazz = PojoGenerator.generate(
                pojoName, fields_name, fields_type);


        //Step-4 :: Reading the source
        DataSet<String> ds = ENV.readTextFile(soucePath);

        //Step-5 :: Mapping data set into object
        DataSet<Object> new_ds = ds.map(new MapFunction<String, Object>() {
            public Object map(String s) throws Exception {
                String [] splitVal = s.split(",");

                Object obj = clazz.getDeclaredConstructor().newInstance();

                for(int i=0; i<fields_name.size(); i++){

                    if(fields_type.get(i) == String.class)
                        clazz.getMethod("set" + fields_name.get(i).substring(0, 1).toUpperCase() + fields_name.get(i).substring(1), fields_type.get(i)).invoke(obj, splitVal[i]);
                    else if(fields_type.get(i) == Integer.class)
                        clazz.getMethod("set" + fields_name.get(i).substring(0, 1).toUpperCase() + fields_name.get(i).substring(1), fields_type.get(i)).invoke(obj, Integer.parseInt(splitVal[i]));
                    else if(fields_type.get(i) == Double.class)
                        clazz.getMethod("set" + fields_name.get(i).substring(0, 1).toUpperCase() + fields_name.get(i).substring(1), fields_type.get(i)).invoke(obj, Double.parseDouble(splitVal[i]));
                    else if(fields_type.get(i) == Float.class)
                        clazz.getMethod("set" + fields_name.get(i).substring(0, 1).toUpperCase() + fields_name.get(i).substring(1), fields_type.get(i)).invoke(obj, Float.parseFloat(splitVal[i]));
                    else if(fields_type.get(i) == Short.class)
                        clazz.getMethod("set" + fields_name.get(i).substring(0, 1).toUpperCase() + fields_name.get(i).substring(1), fields_type.get(i)).invoke(obj, Short.parseShort(splitVal[i]));

                }

                return obj;

            }
        });

        //Step - 6 :: Adding to sink
        new_ds.print();

    }

    public static void parseJSON() throws Exception{
        //For parsing the JSON
        JSONParser jsonParser = new JSONParser();


        Object obj =jsonParser.parse(new FileReader(configFilePath));

        JSONObject config = (JSONObject)obj;

        JSONArray fields = (JSONArray) config.get("fields");

        JSONObject tempObject = null;

        String field_name = "";
        Class<?> field_type = null;

        for(Object field : fields){

            tempObject = (JSONObject) field;
            field_name = tempObject.get("name").toString();
            field_type = returnType(tempObject.get("type").toString());

            fields_name.add(field_name);
            fields_type.add(field_type);
        }

        soucePath = config.get("source").toString();
        pojoName = config.get("PojoName").toString();
    }

    public static Class<?> returnType(String type){


        if(type.equals("Integer")){
            return Integer.class;
        }else if(type.equals("String")){
            return String.class;
        }else if(type.equals("Double")){
            return Double.class;
        }else if(type.equals("Float")){
            return Float.class;
        }else if(type.equals("Short")){
            return Short.class;
        }else {
            return null;
        }
    }
}
