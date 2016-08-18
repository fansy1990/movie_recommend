package algorithm;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import util.Utils;

public class RunSpark {
	
	public static void main(String[] args) throws IllegalArgumentException, IOException {
		//<input> <output> <train_percent> <ranks> <lambda> <iteration>
		String[] inputArgs= new String[]{
				"hdfs://node1:8020/user/root/ratings.dat",
				"hdfs://node1:8020/user/fansy/als_output",
				"0.8",
				"10",
				"10.0",
				"20"
		};
		String[] runArgs=new String[]{
                "--name","ALS Model Train ",
                "--class","als.ALSModelTrainer",
                "--driver-memory","512m",
                "--num-executors", "2",
                "--executor-memory", "512m",
                "--jar","hdfs://node1:8020/user/root/Spark141-als.jar",//
                "--files","hdfs://node1:8020/user/root/yarn-site.xml",
                "--arg",inputArgs[0],
                "--arg",inputArgs[1],
                "--arg",inputArgs[2],
                "--arg",inputArgs[3],
                "--arg",inputArgs[4],
                "--arg",inputArgs[5]
        };
		FileSystem.get(Utils.getConf()).delete(new Path(inputArgs[1]), true);
		Utils.runSpark(runArgs);
	}

	//<input> <output> <train_percent> <ranks> <lambda> <iteration>
	public static boolean runALS(String input,String output,String train_percent,String ranks,String lambda,
			String iteration) throws IllegalArgumentException, IOException{
		String[] runArgs=new String[]{
                "--name","ALS Model Train ",
                "--class","als.ALSModelTrainer",
                "--driver-memory","512m",
                "--num-executors", "2",
                "--executor-memory", "512m",
                "--jar","hdfs://node1:8020/user/root/Spark141-als.jar",//
                "--files","hdfs://node1:8020/user/root/yarn-site.xml",
                "--arg",input,
                "--arg",output,
                "--arg",train_percent,
                "--arg",ranks,
                "--arg",lambda,
                "--arg",iteration
        };
		FileSystem.get(Utils.getConf()).delete(new Path(output), true);
		return Utils.runSpark(runArgs);
	}
	
	
}
