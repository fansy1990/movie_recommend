package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.Sets;

import datastructure.FixSizePriorityQueue;
import model.Movie;

public class Utils {
	public static final String DOUBLECOLON = "::";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	private static final String SUBFIX = "part-00000";
	public static final String output = "hdfs://node1:8020/user/fansy/als_output";
	public static final String MOVIESDATA = "hdfs://node1:8020/user/root/movies.dat";
	public static final String RATINGSDATA = "hdfs://node1:8020/user/root/ratings.dat";

	private static final String userFeaturePath = output + "/userFeatures";
	private static final String productFeaturePath = output + "/productFeatures";
	public static final String RMSEPATH = output + "/rmse/" + SUBFIX;
	private static final int TOPN = 10;

	private static Configuration configuration = null;

	/**
	 * 获取Configuration配置文件
	 * 
	 * @return
	 */
	public static Configuration getConf() {
		if (configuration == null) {

			configuration = new Configuration();
			configuration.setBoolean("mapreduce.app-submission.cross-platform", true);
			configuration.set("fs.defaultFS", "hdfs://node1:8020");
			configuration.set("mapreduce.framework.name", "yarn");
			configuration.set("yarn.resourcemanager.address", "node1:8032");
			configuration.set("yarn.resourcemanager.scheduler.address", "node1:8030");
			configuration.set("mapreduce.jobhistory.address", "node2:10020");
		}

		return configuration;
	}

	/**
	 * 调用Spark
	 * 
	 * @param args
	 * @return
	 */
	public static boolean runSpark(String[] args) {
		try {
			System.setProperty("SPARK_YARN_MODE", "true");
			SparkConf sparkConf = new SparkConf();
			sparkConf.set("spark.yarn.jar", "hdfs://node1:8020/user/root/spark-assembly-1.4.1-hadoop2.6.0.jar");
			sparkConf.set("spark.yarn.scheduler.heartbeat.interval-ms", "1000");

			ClientArguments cArgs = new ClientArguments(args, sparkConf);

			new Client(cArgs, getConf(), sparkConf).run();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 读取文件内容
	 * 
	 * @param outputPath
	 * @return
	 */
	public static String readHDFS(String outputPath) {
		StringBuffer buffer = new StringBuffer();
		try {
			Path path = new Path(outputPath);
			FileSystem fs = FileSystem.get(getConf());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));
			try {
				String line;
				while ((line = br.readLine()) != null) {
					buffer.append(line).append("\n");
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		} catch (Exception e) {
			return null;
		}
		return buffer.toString();
	}

	/**
	 * 预测 如果没有初始化，则进行初始化
	 * 
	 * @param uid
	 * @return
	 */
	public static List<Movie> predict(int uid) {
		if (userFeatures.size() <= 0 || productFeatures.size() <= 0) {
			try {
				userFeatures = getModelFeatures(userFeaturePath);
				productFeatures = getModelFeatures(productFeaturePath);
			} catch (IOException e) {
				return null;
			}
			if (userFeatures.size() <= 0 || productFeatures.size() <= 0) {
				System.err.println("模型加载失败!");
				return null;
			}
		}

		// 使用模型进行预测
		// 1. 找到uid没有评价过的movieIds
		Set<Integer> candidates = Sets.difference((Set<Integer>) allMovieIds, userWithRatedMovies.get(uid));

		// 2. 构造推荐排序堆栈
		FixSizePriorityQueue<Movie> recommend = new FixSizePriorityQueue<Movie>(TOPN);
		Movie movie = null;
		double[] pFeature = null;
		double[] uFeature = userFeatures.get(uid);
		double score = 0.0;
		BLAS blas = BLAS.getInstance();
		for (int candidate : candidates) {
			movie = movies.get(candidate);
			pFeature = productFeatures.get(candidate);
			if(pFeature==null) continue;
			score = blas.ddot(pFeature.length, uFeature, 1, pFeature, 1);
			movie.setRated((float) score);
			recommend.add(movie);
		}

		return recommend.sortedList();
	}

	/**
	 * 加载model user/product features
	 * 
	 * @param userfeaturepath2
	 * @return
	 */
	private static Map<Integer, double[]> getModelFeatures(String featurePath) throws IOException {
		Map<Integer, double[]> features = new HashMap<>();
		Path path = new Path(featurePath);
		FileSystem fs = FileSystem.get(getConf());
		BufferedReader br = null;
		InputStreamReader inputReader = null;
		FileStatus[] files = fs.listStatus(path);
		for (FileStatus file : files) {
			if(file.isDirectory()|| file.getLen()<=0){
				continue;
			}
			try {
				inputReader = new InputStreamReader(fs.open(file.getPath()));
				br = new BufferedReader(inputReader);

				String line;
				String[] words = null;
				int id = -1;
				// id:f1,f2,f3,,,fn
				while ((line = br.readLine()) != null) {
					words = line.split(COLON);
					id = Integer.parseInt(words[0]);
					features.put(id, getDoubleFromString(words[1], COMMA));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				inputReader.close();
				br.close();
			}
		}
		return features;
	}

	/**
	 * 从string 转为double数组
	 * 
	 * @param string
	 * @param splitter
	 * @return
	 */
	private static double[] getDoubleFromString(String string, String splitter) {
		String[] strings = string.split(splitter);
		double[] ddArr = new double[strings.length];
		for (int i = strings.length - 1; i >= 0; i--) {
			ddArr[i] = Double.parseDouble(strings[i]);
		}
		return ddArr;
	}

	/**
	 * 初始化 movies、ratings数据
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		// 读取movies数据到：Map<movieId,Movie-descriptions>
		Path path = new Path(MOVIESDATA);
		FileSystem fs = FileSystem.get(getConf());
		BufferedReader br = null;
		InputStreamReader inputReader = null;
		try {
			inputReader = new InputStreamReader(fs.open(path));
			br = new BufferedReader(inputReader);

			String line;
			String[] words = null;
			int id = -1;
			// MovieID::Title::Genres
			while ((line = br.readLine()) != null) {
				words = line.split(DOUBLECOLON);
				id = Integer.parseInt(words[0]);
				movies.put(id, new Movie(id, words[1], words[2]));
			}
			System.out.println("Movies data size:" + movies.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputReader.close();
			br.close();
		}

		// 读取ratings数据到Map<userid, ratedMoviesId> (not recommended)
		path = new Path(RATINGSDATA);
		try {
			inputReader = new InputStreamReader(fs.open(path));
			br = new BufferedReader(inputReader);

			String line;
			String[] words = null;
			int uid = -1;
			HashSet<Integer> movieIds = null;
			// UserID::MovieID::Rating::Timestamp
			while ((line = br.readLine()) != null) {
				words = line.split(DOUBLECOLON);
				uid = Integer.parseInt(words[0]);
				if (userWithRatedMovies.containsKey(uid)) {
					userWithRatedMovies.get(uid).add(Integer.parseInt(words[1]));

				} else {
					movieIds = new HashSet<>();
					movieIds.add(Integer.parseInt(words[1]));
					userWithRatedMovies.put(uid, movieIds);
				}

			}
			System.out.println("Users data size:" + userWithRatedMovies.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputReader.close();
			br.close();
		}

		allMovieIds = movies.keySet();
	}

	private static Map<Integer, Movie> movies = new HashMap<>();
	private static Map<Integer, Set<Integer>> userWithRatedMovies = new HashMap<>();
	private static Set<Integer> allMovieIds = new HashSet<>();
	private static Map<Integer, double[]> userFeatures = new HashMap<>();
	private static Map<Integer, double[]> productFeatures = new HashMap<>();

	public static void main(String[] args) throws IOException {
		init();

		int uid = 1;
		List<Movie> recMovies = predict(uid);
		for(Movie m:recMovies){
			System.out.println(m);
		}
		System.out.println(recMovies.size());
	}
}
