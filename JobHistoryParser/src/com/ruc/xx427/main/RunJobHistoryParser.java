package com.ruc.xx427.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import com.ruc.xx427.entity.JobHistoryRecord;
import com.ruc.xx427.log.JobHistoryCollector;
import com.ruc.xx427.parser.JobHistoryParser;
/*
 * Class : RunJobHistoryParser
 * Author : xiaohua
 * Time : 2014/11/7 14:25
 * Use  : scan all the history and put them into history file
 */
public class RunJobHistoryParser {

	private static String getJobID(String fileName) {
		StringTokenizer st = new StringTokenizer(fileName, "-");
		st.nextToken();
		String tmp = st.nextToken();
		st = new StringTokenizer(tmp, "/");
		String jobID = "";
		while (st.hasMoreTokens()) {
			jobID = st.nextToken();
		}
		return jobID;
	}
	
	private static String getJobName(String fileName) {
		StringTokenizer st = new StringTokenizer(fileName, "-");
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		return st.nextToken();
	}
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "read log");
		String fileInPath = "hdfs://10.77.50.173:9000/tmp/hadoop-yarn/staging/history/done/2014/10/31/000000/job_1411033758207_0007-1414759852502-root-TeraSort-1414760272311-384-200-SUCCEEDED-default-1414759904260.jhist";
	    String fileOutPath = "I:\\hadoop.txt";
		FileInputFormat.addInputPath(job, new Path(fileInPath));
		FileOutputFormat.setOutputPath(job, new Path(fileOutPath));
		ArrayList<String> list = new ArrayList<String>();
		JobHistoryCollector.listFileNames("hdfs://10.77.50.173:9000/tmp/hadoop-yarn/staging/history/done/", conf, list);
		for (int i = 0; i < list.size(); i++) {
			String log = JobHistoryCollector.getLog(list.get(i), conf);
			String jobID = getJobID(list.get(i));
			String jobName = getJobName(list.get(i));
			JobHistoryRecord.init();
			JobHistoryRecord jobHistRecord = JobHistoryParser.parseLog(log);
			jobHistRecord.setJobID(jobID);
			jobHistRecord.setJobName(jobName);
			jobHistRecord.setJobSize(jobHistRecord.getMapReduceFramework().map_input_records[JobHistoryRecord.MAP]);
			JobHistoryRecord.write(jobHistRecord);
			//test data
//			JobHistoryRecord t = JobHistoryRecord.read(0);
//			t.getFileSystemCounters().printFileSystemCounters();
//			t.getMapReduceFramework().printMapReduceFramework();
//			System.out.println(t.getJobID());
//			System.out.println(t.getJobName());
//			System.out.println(t.getJobSize());
			//close file counter and meta
			JobHistoryRecord.close();
			
		}

	}

}
