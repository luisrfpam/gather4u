package org.ufam.gather4u.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ListSerializer;
import com.alibaba.fastjson.serializer.MapSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GatherSerializer {

    public static class AndroidJsonObjectSerializer implements ObjectSerializer {

        private MapSerializer mapSerializer = new MapSerializer();

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int feature) throws IOException {
            if (object == null) {
                serializer.out.writeNull();
                return;
            }

            org.json.JSONObject jsonObject = (org.json.JSONObject) object;
            if(object != null){
                Map<String , Object> map = json2Map(jsonObject);
                TypeReference typeReference = new TypeReference<Map<String , Object>>(){};
                mapSerializer.write(serializer,map,fieldName,typeReference.getType(), feature);
            }
        }


        Map<String , Object> json2Map(org.json.JSONObject jsonObject){
            if(jsonObject == null) return null;
            Map<String , Object> result = new HashMap<>();
            Iterator<String> stringIterator = jsonObject.keys();
            while (stringIterator.hasNext()){
                String key = stringIterator.next();
                Object value = jsonObject.opt(key);
                if(value != null && value != JSONObject.NULL){
                    result.put(key,value);
                }
            }
            return result;
        }
    }

    public static class AndroidJsonArraySerializer implements ObjectSerializer{

        ListSerializer listSerializer = new ListSerializer();

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int feature) throws IOException {
            if (object == null) {
                serializer.out.writeNull();
                return;
            }

            JSONArray jsonArray = (JSONArray) object;
            List<Object> list = new ArrayList<>();
            for(int i = 0 ; i < jsonArray.length() ; ++i){
                Object item = jsonArray.opt(i);
                if(item == null || item == JSONObject.NULL){
                }else{
                    list.add(item);
                }
            }
            TypeReference typeReference = new TypeReference<ArrayList<Object>>(){};
            listSerializer.write(serializer , list , fieldName , typeReference.getType(), feature);
        }
    }

    public static class AndroidJsonArrayDeserializer implements ObjectDeserializer{

        @Override
        public JSONArray deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            String str = parser.parseObject(String.class);
            if (TextUtils.isEmpty(str)) {
                return null;
            } else {
                try {
                    return new org.json.JSONArray(str);
                } catch (JSONException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }

    public static class CustomArray{

        private JSONArray array;
        private String identifier = "";

        public JSONArray getArray() { return array; }

        public void setArray(JSONArray array) { this.array = array; }

        public String getIdentifier() { return identifier; }

        public void setIdentifier(String identifier) { this.identifier = identifier; }
    }

    public static void JSONArraySerialize( String identifier, JSONArray array){
        SerializeConfig.getGlobalInstance().put(JSONArray.class, new AndroidJsonArraySerializer());

        JSONArray idArray = new JSONArray();
        idArray.put(identifier);
        CustomArray custArray = new CustomArray();
        custArray.setArray( idArray );
        JSON.toJSONString( custArray );
    }

    public static JSONArray JSONArrayDeserialize( String identifier ) {
        ParserConfig.getGlobalInstance().putDeserializer(JSONArray.class, new AndroidJsonArrayDeserializer());
        //CustomArray arrayProduct = JSON.parseObject("{\"array\":[\"a\"]}" , CustomArray.class);
        CustomArray arrayProduct = JSON.parseObject("{\"array\"}" , CustomArray.class);
        if (arrayProduct != null){
            if (arrayProduct.getIdentifier().equalsIgnoreCase(identifier)){
                return arrayProduct.getArray();
            }
        }
        return null;
    }

}
