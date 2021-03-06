

// BJsyn: (Bagel Jsyn) library for sample control using jSyn.
// Oct 5 03.
// V2.2
// by: Amit Pitaru (c) 2003
// You may use this code at will. If you include all/parts of it in your project, please mention where you got it: "Processing-Jsyn tutorial by Amit Pitaru, http://pitaru.com"
// If you modify/expand the code and make it better, please send me improved versions: amit@pitaru.com

package pitaru.sonia_v29b;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.*;
import java.applet.Applet;
import com.softsynth.jsyn.*;

import processing.core.*;



public class BJSyn {
 public static PApplet parent;
 public  static int maxSamples = 1000;
 static public SynthSample[] mySamp = new SynthSample[maxSamples];
 public static SampleReader_16V1[] mySampler = new SampleReader_16V1[maxSamples];
 public static LineOut[] myOut = new LineOut[maxSamples];
 public static PanUnit[] myPan = new PanUnit[maxSamples];
 public static double basePitch;
 public static boolean ifApplication = false;
 public static InputStream stream;
 public static final int NUM_FRAMES = 64;
 public static int count = 0;
 public static LinearLag[] myLinearLag = new LinearLag[maxSamples];
 public static LinearLag[] myLinearLag2 = new LinearLag[maxSamples];
 public static LinearLag[] myLinearLag3 = new LinearLag[maxSamples];
 public static int sampleNum = 0;
 public static int channelNum;
 public static MultiplyUnit[] multiplier = new MultiplyUnit[maxSamples];
 // static BusReader myBusReader = new BusReader();
  //static BusWriter[] myBusWriter = new BusWriter[maxSamples*2];
  //static LineOut mainOut = new LineOut();

// constructor
  public BJSyn() {
    //initJsyn(0);

  }


public static int getChannels(int sampleNum){
	return mySamp[sampleNum].getChannelsPerFrame();

}




// loop the sample, providing start-end points.
  public static void loopSample(int sampleNum , int start, int end) {
    try {
      startCircuit(sampleNum);
      int offset = Synth.getTickCount() + 1; // used to delay operations below - prevents 'pop' sound
      mySampler[sampleNum].samplePort.clear(offset); // reset the sample play-head.
      // in the next 'frame' (offset), start a slope from 0 to 1, over .001 sec.
      // we use these slopes all over the code to prevent 'pop' sounds - and provide a clean transition.
      myLinearLag2[sampleNum].time.set(offset, 0.001);
      myLinearLag2[sampleNum].input.set(offset, 1.0);
      // in the next 'frame' (offset), start looping the sample between start-end points.
      mySampler[sampleNum].samplePort.queueLoop(offset, mySamp[sampleNum], start, end - start);

    } catch (SynthException e) {
      System.out.println(e);

    }
  }

  // loop sample, using entire sample-data.
  public static void loopSample(int sampleNum) {
      loopSample(sampleNum, 0, mySamp[sampleNum].getNumFrames());
    }

  // Play sample, using entire sample-data.
  public static void playSample(int sampleNum) {
    playSample(sampleNum, 0, mySamp[sampleNum].getNumFrames());
  }

  // Play sample once. See loopSample() for details.
  public static void playSample(int sampleNum , int start, int end) {
    try {
      startCircuit(sampleNum);
      int offset = Synth.getTickCount() + 1;
      mySampler[sampleNum].samplePort.clear(offset);
      myLinearLag2[sampleNum].time.set(offset, 0.001);
      myLinearLag2[sampleNum].input.set(offset, 1.0);
      mySampler[sampleNum].samplePort.queue(offset, mySamp[sampleNum], start, end - start);



    } catch (SynthException e) {
      System.out.println(e);

    }
  }


    // Loop sample a number of times. See loopSample() for details.
    public static void loopSampleNum(int num, int sampleNum , int start, int end) {
      try {
        startCircuit(sampleNum);
        int offset = Synth.getTickCount() + 1;
        mySampler[sampleNum].samplePort.clear(offset);
        myLinearLag2[sampleNum].time.set(offset, 0.001);
        myLinearLag2[sampleNum].input.set(offset, 1.0);
        for(int i = 0; i < num; i++){
        	mySampler[sampleNum].samplePort.queue(offset, mySamp[sampleNum], start, end - start);
		}

      } catch (SynthException e) {
        System.out.println(e);

      }
  }


// Stop sample
  public static void stopSample(int sampleNum, boolean stopFlag, int stopOffset) {
    try {
		// shut off the circuit, free -cpu.
      if(stopFlag) stopCircuit(sampleNum, stopOffset);
      // Start a slope from current volume to 0, during 0.005 sec.
      myLinearLag2[sampleNum].time.set(0.005);
      myLinearLag2[sampleNum].input.set(0);
    } catch (SynthException e) {
      System.out.println(e);
    }
  }

// Start the circuit, opening up all sample units.
  public static void startCircuit(int sampleNum) {
    try {
      myPan[sampleNum].start();
      myOut[sampleNum].start();
      //myBusWriter[sampleNum].start();
      mySampler[sampleNum].start();
      multiplier[sampleNum].start();
      myLinearLag[sampleNum].start();
      myLinearLag2[sampleNum].start();
      myLinearLag3[sampleNum].start();
    } catch (SynthException e) {
      SynthAlert.showError(e);
      System.out.println(e);

    }
  }

// Start the circuit, stopping all sample units.
  public static void stopCircuit(int sampleNum, int _offset) {
    try {
	// set delayed operation (offset) for shutting off circuites -prevent 'pop' sound.
      int offset = Synth.getTickCount() + _offset;

      //mySampler[sampleNum].samplePort.clear(offset);

      myPan[sampleNum].stop(offset);
      myOut[sampleNum].stop(offset);
      //myBusWriter[sampleNum].stop(offset);
      mySampler[sampleNum].stop(offset);
      multiplier[sampleNum].stop(offset);
      myLinearLag[sampleNum].stop(offset);
      myLinearLag2[sampleNum].stop(offset);
      myLinearLag3[sampleNum].stop(offset);


    } catch (SynthException e) {
      System.out.println(e);

    }
  }

 public  static void setRate(int sampleNum, float r) {
    mySampler[sampleNum].rate.set(r);
  }

 public static double getRate(int sampleNum) {
  	return mySampler[sampleNum].rate.get();
  }

 public static void setVolume(int sampleNum, float a) {
    myLinearLag[sampleNum].input.set(a);
    myLinearLag[sampleNum].time.set(0.03);
  }



  public static void setVolume(int sampleNum, double a) {
    setVolume(sampleNum, (float) (a));
  }


 public static double getVolume(int sampleNum) {
	return myLinearLag[sampleNum].input.get();
  }

public static void setPan(int sampleNum, float p) {
    myLinearLag3[sampleNum].time.set(0.03);
    myLinearLag3[sampleNum].input.set(p);

  }

public static double getPan(int sampleNum) {
  	  return myLinearLag3[sampleNum].input.get();
  }


 public static int getNumFrames(int sampleNum){
	  return mySamp[sampleNum].getNumFrames();
  }


	public static void buildSamp(int sampleNum, String fileName){
			// determines type of sample (can only read wav or aiff)
		      switch (SynthSample.getFileType(fileName)) {
		        case SynthSample.AIFF:
		       		mySamp[sampleNum] = new SynthSampleAIFF();
		        	break;
		        case SynthSample.WAV:
		        	mySamp[sampleNum] = new SynthSampleWAV();
		       		break;
		        default:
		        	System.err.println("Sonia SAYS: Sample must be a 'Wav' or 'Aiff' file format.");
		        break;
      }
	}

 public static void buildEmptySamp(int sampleNum, int len, int rate){
		mySamp[sampleNum] = new SynthSample(len);
		mySamp[sampleNum].setSampleRate(rate);
	}

// Load a sample and build a circuit for it.
 public static void loadSample(int sampleNum, String filename) {

    try {

	// gets the sample file from a jar of directory.
      //URL sampleURL = BJSyn.class.getResource(filename);
     // stream = sampleURL.openConnection().getInputStream();
        InputStream stream = Sonia.host.openStream(filename);
		buildSamp(sampleNum, filename);

	// if there's data in the sample, load the sample.
      if (mySamp[sampleNum] != null) {
        //loadSample(mySamp[sampleNum], stream);
        mySamp[sampleNum].load(stream);
      }

    } catch (IOException e) {
      System.err.println(e);

    } catch (SecurityException e) {
      System.err.println(e);
    } catch (NullPointerException e){
		 System.err.println("Sonia: Please make sure you have entered the correct Sample file name to load");
	}

    if (mySamp[sampleNum].getChannelsPerFrame() == 1){
		 // Now that the sample is ready, create a circuit for it.
   		 buildCircuit(sampleNum);
   		 channelNum = 1;
	} else if (mySamp[sampleNum].getChannelsPerFrame() == 2){
		channelNum = 2;

		int numShorts = mySamp[sampleNum].getNumFrames() * mySamp[sampleNum].getChannelsPerFrame();
		short[] data = new short[numShorts];
		short[] leftSamples = new short[numShorts/2];
		short[] rightSamples = new short[numShorts/2];
		mySamp[sampleNum].read(data);

		int leftIndex = 0;
		int rightIndex = 0;
		int stereoIndex = 0;

		while( stereoIndex < numShorts)	{
			leftSamples[leftIndex++] = data[stereoIndex++];
			rightSamples[rightIndex++] = data[stereoIndex++];
		}


		buildSamp(sampleNum+1, filename);

		mySamp[sampleNum].clear(0,numShorts/2);
		mySamp[sampleNum].allocate(numShorts/2,1);
		mySamp[sampleNum].write(0, leftSamples,0,numShorts/2);
		buildCircuit(sampleNum);
		setPan(sampleNum,-1f);

		mySamp[sampleNum+1].allocate(numShorts/2,1);
		mySamp[sampleNum+1].write(0, rightSamples,0,numShorts/2);
		buildCircuit(sampleNum+1);
		setPan(sampleNum+1,1f);

	}


  }


// Overload loadSample, primarily used by function above.
 public static  void loadSample(SynthSample sample, InputStream stream) throws IOException {
    sample.load(stream);
  }


// Build a circuit for the sample (this is not a real jSyn circuit, but just my terminology).
// See jSyn tutorial for understandign Unit-Generator techniques used here.
 public static void buildCircuit(int sampleNum) {
    mySampler[sampleNum] = new SampleReader_16V1();
    myOut[sampleNum] = new LineOut();
    //myBusWriter[sampleNum] = new BusWriter();
    myPan[sampleNum] = new PanUnit();
    multiplier[sampleNum] = new MultiplyUnit();

    myLinearLag[sampleNum] = new LinearLag();
    myLinearLag2[sampleNum] = new LinearLag();
    myLinearLag3[sampleNum] = new LinearLag();

    myLinearLag3[sampleNum].output.connect(myPan[sampleNum].pan);

    mySampler[sampleNum].output.connect(myPan[sampleNum].input);
    myPan[sampleNum].output.connect(0, myOut[sampleNum].input, 0);
    myPan[sampleNum].output.connect(1, myOut[sampleNum].input, 1);

   // myPan[sampleNum].output.connect(0,myBusWriter[sampleNum].input);
    //myPan[sampleNum].output.connect(0,myBusWriter[sampleNum*2].input);
   // myBusWriter[sampleNum].busOutput.connect( myBusReader.busInput );
	//myBusWriter[sampleNum*2].busOutput.connect( myBusReader.busInput );


    myLinearLag[sampleNum].output.connect(multiplier[sampleNum].inputB);
    myLinearLag2[sampleNum].output.connect(multiplier[sampleNum].inputA);

    multiplier[sampleNum].output.connect(mySampler[sampleNum].amplitude);

    startCircuit(sampleNum);
  }


/*
// Initialize jSyn engine.
  static void initJsyn(int flag) {



	// Only start if not already in use (specific to this code - not usually needed).
    if (Synth.openCount == 0){
      try {
        Synth.startEngine(flag);
        Synth.verbosity = Synth.SILENT;

        //stopEngine se = new stopEngine();


      } catch (SynthException e) {
        System.err.println(e);
      }
    }
  }
  */

// delete a circuit.
 public static void deleteCircuit(int sampleNum) {
    mySampler[sampleNum].delete();
    mySampler[sampleNum] = null;
    myOut[sampleNum].delete();
    myOut[sampleNum] = null;
    //myBusWriter[sampleNum].delete();
    //myBusWriter[sampleNum] = null;

    myPan[sampleNum].delete();
    myPan[sampleNum] = null;
    mySamp[sampleNum].delete();
    mySamp[sampleNum] = null;
    multiplier[sampleNum].delete();
    multiplier[sampleNum] = null;
    myLinearLag[sampleNum].delete();
    myLinearLag[sampleNum] = null;
    myLinearLag2[sampleNum].delete();
    myLinearLag2[sampleNum] = null;
    myLinearLag3[sampleNum].delete();
    myLinearLag3[sampleNum] = null;

  }

// Start all circuits.
 public static  void startEngine() {
    for (int sampleNum = 0; sampleNum < count; sampleNum++) {
      startCircuit(sampleNum);
    }
  }

// Stop all circuits
 public static  void stopEngine() {
	 // System.out.println("count: " + count);
    for (int sampleNum = 0; sampleNum < count; sampleNum++) {
      stopCircuit(sampleNum, 0);
      deleteCircuit(sampleNum);
    }
  }

// called when applet exits.
 public static void stop() {
    try {

      //Delete unit peers.
      stopEngine();
      count = 0; // ie java and mac don't do this on restart...
 	  //Turn off tracing.
      Synth.setTrace(Synth.SILENT);
      //Stop synthesis engine.
      //Synth.stopEngine();
    } catch (SynthException e) {
      System.err.println(e);
    }
  }

}

