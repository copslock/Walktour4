package de.opticom.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class PolqaJobList implements Cloneable {
	public String batchFileName = "";
	public String resultFileName = "";
	public PolqaJob[] jobList = new PolqaJob[0];

	public void fillSingle(
			String referenceFile,
			String testFile,
			boolean superwideband,
			int ituVersion,
			boolean disableLevelAlignment,
			boolean disableSrConversion,
			boolean enableHpMode,
			int numberOfRepititions)
	{
		batchFileName = "";
		resultFileName = "";
		jobList = new PolqaJob[numberOfRepititions];
		
		for (int i=0; i<numberOfRepititions; ++i) {
			jobList[i] = new PolqaJob();
			jobList[i].input.referenceFilename = referenceFile;
			jobList[i].input.testFilename = testFile;
			jobList[i].input.superwideband = superwideband;
			jobList[i].input.ituVersion = ituVersion;
			jobList[i].input.sampleRate = -1;
			jobList[i].input.disableLevelAlignment = disableLevelAlignment;
			jobList[i].input.disableSrConversion = disableSrConversion;
			jobList[i].input.enableHaMode = enableHpMode;
		}
	}

	public void fillBatch(
			String batchFileName,
			int numberLinesToSkip,
			int numberOfRepetitions,
			int ituVersion,
			boolean disableLevelAlignment,
			boolean disableSrConversion,
			boolean enableHpMode)
	{
		this.batchFileName = batchFileName;
		this.resultFileName = this.batchFileName + ".tab";
		
		File f = new File(this.batchFileName);
		String pathname = f.getParent();
		f = null;
		
		ArrayList<PolqaJob> jobs = new ArrayList<PolqaJob>();
		
		FileReader fileReader = null;
		LineNumberReader lineNumberReader = null;
		try {
			fileReader = new FileReader(this.batchFileName);
			lineNumberReader = new LineNumberReader(fileReader);
			lineNumberReader.readLine();
			while(true) {
				String line = lineNumberReader.readLine();
				if(line==null) break; // EOF
				if(lineNumberReader.getLineNumber() <= numberLinesToSkip) continue;
				String[] records = line.split("[\t;]");
				if(records.length < 17 || records[0].length()==0 || records[1].length()==0) {
					continue;
				}
				
				for(int i=0; i<numberOfRepetitions; ++i) {
					PolqaJob job = new PolqaJob();
					String pathname2 = records[2];
					job.input.referenceFilename = (pathname + "\\" + pathname2 + "\\" + records[0]).replace('\\', File.separatorChar);
					job.input.testFilename = (pathname + "\\" + pathname2 + "\\" + records[1]).replace('\\', File.separatorChar);
					job.input.sampleRate = Integer.parseInt(records[15].toString());
					job.input.superwideband = records[16].equals("SWB");
					job.input.ituVersion = ituVersion;
					job.input.passthroughData = pathname2;
					job.input.disableLevelAlignment = disableLevelAlignment;
					job.input.disableSrConversion = disableSrConversion;
					job.input.enableHaMode = enableHpMode;
					job.input.skriptLine = records;
					jobs.add(job);
				}
			}	
			jobList = jobs.toArray(new PolqaJob[0]);
		
		} catch (Exception e) {
			throw new InternalError(e.toString());
		} finally {
			try {
				fileReader.close();
				lineNumberReader.close();
			}
			catch (IOException e) {	}
		}
	}
	
	public void logResult(int i, boolean append) {
		if (resultFileName.equals("")) return;
		FileWriter fw = null;
		try {
			fw = new FileWriter(resultFileName, append);

			int format = 1;
			switch (format) {
			case 1:
				fw.write(String.format("%s\t%s\t%.4f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%d\t%s\t%d\n",
						jobList[i].input.referenceFilename,
						jobList[i].input.testFilename,
						jobList[i].result.mfMOSLQO,
						jobList[i].result.mfAvgDelay,
						jobList[i].result.mfAttenuation,
						(double)jobList[i].result.nrSamplesRef/(double)jobList[i].result.sampleRateRef,
						(double)jobList[i].result.nrSamplesDeg/(double)jobList[i].result.sampleRateDeg,
						jobList[i].result.polqaRunDuration,
						jobList[i].input.sampleRate,
						jobList[i].input.superwideband ? 1 : 0,
						jobList[i].input.passthroughData,
						jobList[i].result.result));
				break;

			case 2:
				if (jobList[i].input.skriptLine != null) {
					StringBuilder sb = new StringBuilder();
					for (int j=0; j<17; ++j) {
						sb.append(jobList[i].input.skriptLine[j]);
						sb.append("\t");
					}
					sb.append(String.format("%.4f", jobList[i].result.mfMOSLQO)); sb.append("\t");
					sb.append(String.format("%d", jobList[i].result.nrSamplesRef)); sb.append("\t");
					sb.append(String.format("%d", jobList[i].result.sampleRateRef)); sb.append("\t");
					sb.append(String.format("%d", jobList[i].result.nrSamplesDeg)); sb.append("\t");
					sb.append(String.format("%d", jobList[i].result.sampleRateDeg)); sb.append("\t");
					sb.append(String.format("%.4f", jobList[i].result.polqaRunDuration)); sb.append("\t");
					sb.append("\n");
					fw.write(sb.toString());
				}
				break;

			}

		} catch (IOException e) {
		} finally {
			try { fw.close(); } catch (IOException e) {}
		}
	}

	
}
