package com.mengma.utils;

import java.util.*;

/**
 * @author fgm
 * @description  自带过期时间的map
 * @date 2020-04-25
 ***/
public class ExpiryMap<K,V> extends HashMap<K,V> {

    private static final long serialVersionUID = -1937850576602303086L;
    //默认超时时间
    private static Long EXPIRY = 2*1000L;
    /**
     * 记录key的缓存时间
     */
    private HashMap<K, Long> expiryMap = new HashMap();

    /**  缓存实例对象 */
    private volatile static ExpiryMap<String, String> ConcreteMap;

    public static ExpiryMap getInstance(){
        if(null == ConcreteMap){
            synchronized(ExpiryMap.class){
                if(null==ConcreteMap){
                    ConcreteMap=new ExpiryMap();
                }
            }
        }
        return ConcreteMap;
    }


    public ExpiryMap() {
        super();
    }
    public ExpiryMap(long expireTime){
        this(1<<4,expireTime);
    }
    public ExpiryMap(int capacity,Long expireTime){
        super(capacity);
        this.EXPIRY = expireTime;
    }

    @Override
    public V put(K key,V value){
       return put(key,value,this.EXPIRY);
    }

    public V put(K key, V value, long expiryTime) {
        expiryMap.put(key, System.currentTimeMillis() + expiryTime);
        return super.put(key, value);
    }

    @Override
    public int size() {
        return entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return entrySet().size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return !checkExpiry(key, true) && super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if(null==value){
            return Boolean.FALSE;
        }
        Set<Entry<K, V>> set = super.entrySet();
        Iterator<Entry<K, V>> iterator = set.iterator();
        while(iterator.hasNext()){
            Entry<K, V> entry = iterator.next();
            K key= entry.getKey();
            V entryValue = entry.getValue();
            if(entryValue.equals(value)){
                if(checkExpiry(key,false)){
                    iterator.remove();
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = super.values();
        if(values == null || values.size() < 1) {
            return values;
        }
        Iterator<V> iterator = values.iterator();
        while (iterator.hasNext()) {
            V next = iterator.next();
            if(!containsValue(next)) {
                iterator.remove();
            }
        }
        return values;
    }
    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        if(checkExpiry(key, true)) {
            return null;
        }
        return super.get(key);
    }
    /**
     *
     * @Description: 是否过期
     * @param key
     * @return null:不存在或key为null -1:过期  存在且没过期返回value 因为过期的不是实时删除，所以稍微有点作用
     */
    public Object isInvalid(Object key) {
        if (key == null) {
            return null;
        }
        if(!expiryMap.containsKey(key)){
            return null;
        }
        long expiryTime = expiryMap.get(key);

        boolean flag = System.currentTimeMillis() > expiryTime;

        if(flag){
            super.remove(key);
            expiryMap.remove(key);
            return -1;
        }
        return super.get(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            expiryMap.put(e.getKey(), System.currentTimeMillis() + EXPIRY);
        }
        super.putAll(m);
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        Set<java.util.Map.Entry<K, V>> set = super.entrySet();
        Iterator<Entry<K, V>> iterator = set.iterator();
        while (iterator.hasNext()) {
            java.util.Map.Entry<K, V> entry = iterator.next();
            if(checkExpiry(entry.getKey(), false)) {
                iterator.remove();
            }
        }

        return set;
    }

    private boolean checkExpiry(Object key,boolean isRemoveSuper) {
        if(!expiryMap.containsKey(key)){
            return Boolean.FALSE;
        }
        long expiryTime = expiryMap.get(key);
        boolean flag = System.currentTimeMillis() > expiryTime;
        if(flag){
            if(isRemoveSuper) {
                super.remove(key);
            }
            expiryMap.remove(key);
        }
        return flag;

    }

    public static void main(String[] args) throws InterruptedException {

        ExpiryMap<String, String> map = new ExpiryMap();
        map.put("test", "xxx");
        map.put("test2", "ankang", 5000);
        System.out.println("test==" + map.get("test"));
        Thread.sleep(3000);
        System.out.println("test==" + map.get("test"));
        System.out.println("test2==" + map.get("test2"));
        Thread.sleep(3000);
        System.out.println("test2==" + map.get("test2"));
    }

}
