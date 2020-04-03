import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.*;


/**
 * The class will be responsible for generating the POJO based on provided schema
 */
public class PojoGenerator {

    /**
     * The method will generate a POJO on fly.
     *
     * @param className represents the name of the POJO class that needs to be generated e.g. Student, Person etc.
     * @param fieldNames represents the fields that should by part of POJO e.g. firstName, lastName etc.
     * @param fieldsType represents the type of the fields e.g. Integer, String etc.
     * @return the POJO having properties specified in 'fieldNames' parameter having types as specified in 'fieldTypes'
     *         parameter.
     * @throws NotFoundException when the program could not get the ClassPool
     * @throws CannotCompileException when POJO compilation fails
     */
    public static Class generate(String className, List<String> fieldNames, List<Class<?>>  fieldsType)
            throws NotFoundException, CannotCompileException {

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(className);


        for(int i=0; i<fieldNames.size(); i++){

            cc.addField(new CtField(resolveCtClass(fieldsType.get(i)), fieldNames.get(i), cc));

            // add getter
            cc.addMethod(generateGetter(cc, fieldNames.get(i), fieldsType.get(i)));

            // add setter
            cc.addMethod(generateSetter(cc, fieldNames.get(i), fieldsType.get(i)));
        }
        cc.addMethod(generateToString(cc, fieldNames, fieldsType));
        return cc.toClass();
    }


    /**
     * The function will generate toString() method for the POJO.
     *
     * @param declaringClass represents the class to which the toString() method belongs to.
     * @param fieldNames represents the fields associated with POJO.
     * @param fieldsType represents the types of the associated fields
     * @return the method to be added in the class
     * @throws CannotCompileException when compilation fails
     */
    private static CtMethod generateToString(CtClass declaringClass, List<String> fieldNames, List<Class<?>>  fieldsType)
            throws CannotCompileException {


        StringBuffer sb = new StringBuffer();
        sb.append("public String toString(){ return (");


        for(int i=0; i<fieldNames.size(); i++){
            sb.append("this.")
                .append(fieldNames.get(i));

                if( i == fieldNames.size()-1)
                    sb.append(");");
                else
                    sb.append(" + \" || \" +");
        }
        sb.append("}");
        return CtMethod.make(sb.toString(), declaringClass);
    }

    /**
     * The function will generate the getter method that will belong to the POJO.
     *
     * @param declaringClass represents the class to which the getter method belongs to.
     * @param fieldName represents the name of the field whose getter is to be created.
     * @param fieldClass represents the type of respective field.
     * @return the getter method for the POJO
     * @throws CannotCompileException when the compilation fails
     */
    private static CtMethod generateGetter(CtClass declaringClass, String fieldName, Class fieldClass)
            throws CannotCompileException {

        String getterName = "get" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        StringBuffer sb = new StringBuffer();
        sb.append("public ").append(fieldClass.getName()).append(" ")
                .append(getterName).append("(){").append("return this.")
                .append(fieldName).append(";").append("}");
        return CtMethod.make(sb.toString(), declaringClass);
    }

    /**
     * The function will generate the setter method that will belong to the POJO.
     *
     * @param declaringClass represents the class to which the setter method belongs to.
     * @param fieldName represents the name of the field whose setter is to be created.
     * @param fieldClass represents the type of respective field.
     * @return the setter method for the POJO
     * @throws CannotCompileException when the compilation fails
     */
    private static CtMethod generateSetter(CtClass declaringClass, String fieldName, Class fieldClass)
            throws CannotCompileException {

        String setterName = "set" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        StringBuffer sb = new StringBuffer();
        sb.append("public void ").append(setterName).append("(")
                .append(fieldClass.getName()).append(" ").append(fieldName)
                .append(")").append("{").append("this.").append(fieldName)
                .append("=").append(fieldName).append(";").append("}");
        return CtMethod.make(sb.toString(), declaringClass);
    }

    /**
     * The method will return the class pool.
     *
     * @param clazz The class of the attribute
     * @return The pool of the class
     * @throws NotFoundException when no pool found against the class
     */
    private static CtClass resolveCtClass(Class clazz) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();

        return pool.get(clazz.getName());
    }
}