/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package apriorialgorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsh
 */
 class AprioriAlgorithm {

    /**
     * @param args the command line arguments
     */
    static TreeMap<String, HashSet<Integer>> oneCount;
    static HashMap< String,String> metaData;
    static List<List<String>> TData;
    static TreeMap<String, HashSet<Integer>> prevCount;
    static float support;
    static float supValue;
    static float confidence;
    static int flag;
    static int frequentDataSetCount=0;
    static int associationRuleCount=0;
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            metaData = new HashMap<>();
            TData= new ArrayList<>();
            BufferedReader metadataHandle = new BufferedReader(new FileReader("config.csv"));
            String nextRow=metadataHandle.readLine();
            while(nextRow!=null)
            {
                StringTokenizer st = new StringTokenizer(nextRow,",");
                metaData.put(st.nextToken(), st.nextToken());
                nextRow=metadataHandle.readLine();
            }
             /*for(Map.Entry m:metaData.entrySet()){  
                System.out.println(m.getKey()+" "+m.getValue());  
             }*/
            support=Float.parseFloat(metaData.get("support"));
            confidence=Float.parseFloat(metaData.get("confidence"));
            flag=Integer.parseInt(metaData.get("flag"));
            readTData();
            //System.out.println(support);
            supValue=(support*supValue);
            //System.out.println("=========="+conValue+"============");
            generateFrequentSets();
            //writeValue(frequentDataSetCount);
            generateOutputFile1();
            /*for(Map.Entry m:prevCount.entrySet()){  
                System.out.println(m.getKey()+" "+m.getValue());  
             }*/
            if(flag==1 && prevCount.size()>0)  {
                generateAssociationRules();
                // writeValue(associationRuleCount);
                generateOutputFile2();
            }
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readTData() {
            
        BufferedReader TDataHandle = null;
        try {
            TDataHandle = new BufferedReader(new FileReader(metaData.get("input")));
            String nextRow=TDataHandle.readLine();
            int count=0;
            while(nextRow!=null)
            {
                List<String> temp=new ArrayList<>();
                StringTokenizer st = new StringTokenizer(nextRow,",");
                while(st.hasMoreElements())
		{
                    String token = st.nextToken();
                    temp.add(token);
                }
                TData.add(temp);
                nextRow=TDataHandle.readLine();
                count++;
            }
            supValue=count;
            /*for (List<String> l : TData) {
                for(String s:l)
                {
                    System.out.print(s);
                }
                System.out.println();
            }*/
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                TDataHandle.close();
            } catch (IOException ex) {
                Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void generateFrequentSets() {
        
        /*********************size 1*************/
            oneCount=new TreeMap<>();
            int i=1;
            for (List<String> l : TData) {
                for(String s:l)
                {
                    HashSet<Integer> set = oneCount.get(s); 
                    if (set == null) { set = new HashSet<>();set.add(i);oneCount.put(s, set);}
                    else {set.add(i);}
                }
                i++;
            }
            //TreeMap<String, HashSet<Integer>> prevCount=new TreeMap<>();
            prevCount=new TreeMap<>();
            for(Map.Entry m:oneCount.entrySet()){  
                  HashSet<Integer> set = (HashSet<Integer>) m.getValue(); 
                  //System.out.println(supValue);
                  if(set.size()>=supValue) {prevCount.put((String)m.getKey(), set);}
             }
            
            /*for(Map.Entry m:oneCount.entrySet()){  
                System.out.println(m.getKey()+" "+m.getValue());  
             }*/
            writeFrequentSet(prevCount);
            while(prevCount.size()>1)
            {
                //System.out.println(prevCount.size());
                List<String> keys= new ArrayList<>(prevCount.keySet());
                List<HashSet<Integer>> valueSet=new ArrayList<>(prevCount.values());
                TreeMap<String, HashSet<Integer>> currCount=new TreeMap<>();
                for(int j=0;j<keys.size();j++)
                {
                    //System.out.println(keys.get(j)+"=");
                    String key1=keys.get(j);
                    int index=key1.lastIndexOf(",");
                    String firstPart1=key1.substring(0, index+1);
                        //System.out.println(firstPart);
                    String secondPart1=key1.substring(index+1);
                    HashSet<Integer> set1=valueSet.get(j);
                    for(int k=j+1;k<keys.size();k++)
                    {
                        //System.out.println(j+"="+k);
                        String key2=keys.get(k);
                        int index2=key2.lastIndexOf(",");
                        String firstPart2=key2.substring(0, index2+1);
                        //System.out.println(firstPart);
                        String secondPart2=key2.substring(index2+1);
                        //System.out.println(firstPart1+"++"+firstPart2);
                        if(!firstPart1.equals(firstPart2)) 
                        {//System.out.println("1");
                        continue;
                        }
                        HashSet<Integer> set2=valueSet.get(k);
                        String newKey=key1+","+secondPart2;
                        Set<Integer> newSet = new HashSet<>(set1);
                        newSet.retainAll(set2);
                        currCount.put(newKey, (HashSet<Integer>) newSet);                       
                    }
                }
                if(currCount.size()>0) prevCount.clear();
                else break;
                for(Map.Entry m:currCount.entrySet()){  
                  HashSet<Integer> set = (HashSet<Integer>) m.getValue(); 
                  //System.out.println(supValue);
                  //if(set.size()>=Math.floor(supValue)) 
                  if(set.size()>=supValue) 
                  {
                    prevCount.put((String)m.getKey(), set);
                  }
             }
                //System.out.println(prevCount.size());
                writeFrequentSet(prevCount);
                for(Map.Entry m:prevCount.entrySet()){  
                //System.out.println(m.getKey()+" "+m.getValue());  
             }
            }
//            return prevCount;
            }

    private static void writeFrequentSet(TreeMap<String, HashSet<Integer>> prevCount) {
        PrintWriter pr=null;
        try {
           // pr =new PrintWriter(new BufferedWriter(new FileWriter(metaData.get("output"), true)));
             pr =new PrintWriter(new BufferedWriter(new FileWriter("temp", true)));
            for(Map.Entry m:prevCount.entrySet()){
                //System.out.println(m.getKey()+" "+m.getValue());
                frequentDataSetCount++;
                pr.println(m.getKey());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pr.close();
        }
    }

    private static void generateAssociationRules() {
        int numerator;
        int denominator;
        /*for(Map.Entry m:oneCount.entrySet()){  
                System.out.println(m.getKey()+" "+m.getValue());  
             }*/
        //System.out.println(prevCount.size());
        for(Map.Entry m:prevCount.entrySet()){ 
                //System.out.println(m.getKey());  
                HashSet<Integer> HS=(HashSet<Integer>)m.getValue();
                //System.out.println(HS);
                numerator=HS.size();
                //System.out.println(numerator);
                List<String> temp=new ArrayList<>();
                StringTokenizer st = new StringTokenizer((String) m.getKey(),",");
                while(st.hasMoreElements())
		{
                    String token = st.nextToken();
                    temp.add(token);
                }
                Set<Set<String>> powerSet = getPowerset(temp);
                
                //System.out.println(powerSet.size());
                /*for(Set<String> set:powerSet)
                {
                    for(String item:set) System.out.print("++"+item);
                    System.out.println();
                }*/
                for(Set<String> set:powerSet)
                {
                    if(set.size()==0 || set.size()==temp.size()) {;}
                    else{ 
                    /*
                    HashSet<Integer> set2=valueSet.get(k);
                        String newKey=key1+","+secondPart2;
                        Set<Integer> newSet = new HashSet<>(set1);
                        newSet.retainAll(set2);
                    */
                    int count=0;
                    HashSet<Integer> originalSet=new HashSet<>();
                    
                    for(String item:set)
                    {
                        HashSet<Integer> tempSet=new HashSet<Integer>(oneCount.get(item));
                        if(count==0) {count++;originalSet= tempSet;}
                        else
                        {
                            originalSet.retainAll(tempSet);
                        }
              //          System.out.print(item+" "+oneCount.get(item).size()+"==");
                    }
                        System.out.println();
                    denominator=originalSet.size();
                    //System.out.println(denominator);
                    float conf=(float)numerator/(float)denominator; 
                    if(conf>=confidence)
                    {
                        writeAssociationRule(set,temp,conf);
                    }
                }
                }    
             }
        /*for(Map.Entry m:oneCount.entrySet()){  
                System.out.println(m.getKey()+" "+m.getValue());  
             }*/
    }

    private static Set<Set<String>> getPowerset(List<String> temp) {
        HashSet<Set<String>> powerSet = new HashSet<>();
        int n=temp.size();
        //System.out.println(temp.size());
        for( long i = 0; i < (1 << n); i++) {
        Set<String> element = new HashSet<>();
        for( int j = 0; j < n; j++ ) {if( (i >> j) % 2 == 1 ) element.add(temp.get(j));}
        powerSet.add(element); 
        }
return powerSet;
}

    private static void writeAssociationRule(Set<String> set, List<String> temp,float conf) {
        //System.out.println(set);
        //System.out.println(temp);
        Set<String> RHS=new HashSet<>();
        for(String s:temp)
        {
            if(set.contains(s)==false) {RHS.add(s);}
        }
        //System.out.print(set+"==>"); System.out.print(RHS);System.out.println();
        int size=0,count=0;
        PrintWriter pr=null;
        try {
            //pr =new PrintWriter(new BufferedWriter(new FileWriter(metaData.get("output"), true)));
            pr =new PrintWriter(new BufferedWriter(new FileWriter("temp", true)));
            size=set.size();
            for(String s:set){
                pr.print(s);
                count++;
                if(count<size) pr.print(",");
            }
            pr.print("=>");
            associationRuleCount++;
            count=0;size=RHS.size();
            for(String s:RHS){
                pr.print(s);
                count++;
                if(count<size) pr.print(",");
            }
            //pr.print(" "+conf);
            pr.println();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pr.close();
        }
    }

    private static void writeValue(int value) {
        PrintWriter pr=null;
        try {
            //pr =new PrintWriter(new BufferedWriter(new FileWriter(metaData.get("output"), true)));
            pr =new PrintWriter(new BufferedWriter(new FileWriter("temp", true)));
            pr.println(value);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pr.close();
        }
    }

    private static void generateOutputFile1() {
        try {
            BufferedWriter out1 = new BufferedWriter
              (new FileWriter(metaData.get("output"),true));
            //System.out.println(frequentDataSetCount);
            String s=Integer.toString(frequentDataSetCount);
            out1.write(s+"\n");
            String str;
            BufferedReader in1 = new BufferedReader(new FileReader("temp"));
             while ((str = in1.readLine()) != null) {
                 out1.write(str+"\n");
      }
             out1.close();
             boolean success = (new File("temp")).delete();
             
             if (success) {
            //System.out.println("The file has been successfully deleted"); 
         }
            
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateOutputFile2() {
          try {
            BufferedWriter out1 = new BufferedWriter
              (new FileWriter(metaData.get("output"),true));
            //System.out.println(frequentDataSetCount);
            String s=Integer.toString(associationRuleCount);
            out1.write(s+"\n");
            String str;
            BufferedReader in1 = new BufferedReader(new FileReader("temp"));
             while ((str = in1.readLine()) != null) {
                 out1.write(str+"\n");
      }
             out1.close();
             boolean success = (new File("temp")).delete();
             
             if (success) {
            //System.out.println("The file has been successfully deleted"); 
         }
            
        } catch (IOException ex) {
            Logger.getLogger(AprioriAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
