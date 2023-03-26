//Masters project version 2.0 - 11th March
package com.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class App {
    public ArrayList<Measure> extractInformation2() {
        ArrayList<Measure> measureArrayList = new ArrayList<Measure>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            // Check if original file exists
            DocumentBuilder builder = factory.newDocumentBuilder();
            String path = "Melody_C_for_testing_version2.musicxml";
            File f = new File(path);
            if (!f.exists()) {
                System.out.println("File does not exists");
                return null;
            }

            // If original file exists, copy it to tempMusic.xml file.
            File file = new File("tempMusic.xml");
            boolean result = file.createNewFile();
            // Copy contents of original file to tempMusic.xml file.
            copyContent(f, file);

            // Remove first two lines of tempMusic.xml file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file + "tmp.xml"));
            reader.readLine();
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
            reader.close();
            writer.close();
            File originalFile = file;
            File tempFile = new File(file + "tmp.xml");
            originalFile.delete();
            tempFile.renameTo(originalFile);
            System.out.println("The first two lines have been deleted from the file.");
            System.out.println("------------------------------------------------------------------");

            Document document = builder.parse(file);

            // Normalize the xml structure
            document.getDocumentElement().normalize();

            // Get all elements by tag name
            NodeList measureList = document.getElementsByTagName("measure");
            outlook:
            
            // Iterating through each measure
            for (int i = 0; i < measureList.getLength(); i++) 
            {
                Measure measureObj = new Measure();
                Node measure = measureList.item(i);
                if (measure.getNodeType() == Node.ELEMENT_NODE) 
                {
                    // Getting children of measure
                    NodeList measureChildList = measure.getChildNodes();
                    // Harmony and notelist object creation
                    String rootStep = "C";
                    String kind = "";
                    ArrayList<Note> noteArrayList = new ArrayList<>();
                    Harmony harmony = new Harmony();
                    String step = "";
                    String duration = "";
                    String type = "";
                    String beam = "";

                    for (int j = 0; j < measureChildList.getLength(); j++)
                    {
                        Node measureChild = measureChildList.item(j);
                        Note noteObj = new Note();
                        if (measureChild.getNodeType() == Node.ELEMENT_NODE) 
                        {
                            Element measureChildElement = (Element) measureChild;
                            if (measureChildElement.getNodeName().equals("harmony")) 
                            {
                                // Getting list of harmonys
                                NodeList harmonyList = measureChildElement.getChildNodes();
                                for (int k = 0; k < harmonyList.getLength(); k++) // [root, kind, staff]
                                {
                                    // Get specific harmony from the harmony list
                                    Node harmonyChild = harmonyList.item(k);
                                    if (harmonyChild.getNodeType() == Node.ELEMENT_NODE) 
                                    {
                                        Element harmonyChildElement = (Element) harmonyChild;
                                        if (harmonyChildElement.getNodeName().equals("root")) 
                                        {
                                            // Get list of all roots
                                            NodeList rootList = harmonyChildElement.getChildNodes();
                                            for (int l = 0; l < rootList.getLength(); l++) // [step, octave]
                                            {
                                                // get specific root from the root list
                                                Node rootChild = rootList.item(l);
                                                if (rootChild.getNodeType() == Node.ELEMENT_NODE) 
                                                {
                                                    Element rootChildElement = (Element) rootChild;
                                                    if (rootChildElement.getNodeName().equals("root-step")) 
                                                    {
                                                        rootStep = rootChildElement.getTextContent();
                                                    }
                                                }
                                            }
                                        }
                                        if (harmonyChildElement.getNodeName().equals("kind")) 
                                        {
                                            kind = harmonyChildElement.getTextContent();
                                        }
                                    }
                                }
                                //harmony.setRootStep(rootStep);
                                //harmony.setKind(kind);
                            } 
                            else if (measureChildElement.getNodeName().equals("note")) 
                            {
                                NodeList noteList = measureChildElement.getChildNodes();
                                for (int k = 0; k < noteList.getLength(); k++) // [pitch, duration, instrument,voice,type, stem, staff]
                                {
                                    // Getting a specific note
                                    Node noteChild = noteList.item(k);
                                    if (noteChild.getNodeType() == Node.ELEMENT_NODE) {
                                        Element noteChildElement = (Element) noteChild;
                                        if (noteChildElement.getNodeName().equals("rest")) {
                                            step = "c";
                                        }
                                        if (noteChildElement.getNodeName().equals("pitch")) {
                                            // Getting a list of pitch
                                            NodeList pitchList = noteChildElement.getChildNodes();
                                            for (int l = 0; l < pitchList.getLength(); l++) // [step, octave]
                                            {
                                                // getting a specific pitch
                                                Node pitch = pitchList.item(l);
                                                if (pitch.getNodeType() == Node.ELEMENT_NODE) {
                                                    Element pitchChildElement = (Element) pitch;
                                                    if (pitchChildElement.getNodeName().equals("step")) {
                                                        step = pitchChildElement.getTextContent();
                                                    }
                                                }
                                            }
                                        }
                                        if (noteChildElement.getNodeName().equals("duration")) {
                                        	duration = noteChildElement.getTextContent();
                                        }
                                        if (noteChildElement.getNodeName().equals("type")) {
                                            type = noteChildElement.getTextContent();
                                        }
                                        if (noteChildElement.getNodeName().equals("beam")) {
                                            beam = noteChildElement.getTextContent();
                                        }
                                    }
                                }
                                noteObj.setStep(step);
                                noteObj.setDuration(duration);
                                noteObj.setType(type);
                                noteObj.setBeam(beam);
                                noteArrayList.add(noteObj);
                            }
                            else if (measureChildElement.getNodeName().equals("barline")) 
                            {
                            	break outlook;
                            }
                        }
                        harmony.setRootStep(rootStep);
                        harmony.setKind(kind);
                        measureObj.setHarmony(harmony);
                        measureObj.setNoteList(noteArrayList);
                    }
                }
                measureArrayList.add(measureObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return measureArrayList;
    }
    public static void copyContent(File a, File b) throws Exception {
        FileInputStream in = new FileInputStream(a);
        FileOutputStream out = new FileOutputStream(b);
        try {
            int n;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        System.out.println("File Copied");
    }

    public static void main(String[] args) {
        App app = new App();
        
        // Extract information from musicxml
        ArrayList<Measure> measureList = app.extractInformation2();

        /*for (int s = 0; s < measureList.size(); s++) {
            System.out.println("Measure ->");

            System.out.println("Harmony : ");

            System.out.println("rootstep : " + measureList.get(s).getHarmony().getRootStep());
            System.out.println("kind : " + measureList.get(s).getHarmony().getKind());

            System.out.println(s+1);
            for (int t = 0; t < measureList.get(s).getNoteList().size(); t++) {
                System.out.println("Step : " + measureList.get(s).getNoteList().get(t).getStep());
                System.out.println("Duration : " + measureList.get(s).getNoteList().get(t).getDuration());
                System.out.println("Type : " + measureList.get(s).getNoteList().get(t).getType());
                System.out.println("Beam : " + measureList.get(s).getNoteList().get(t).getBeam());
                System.out.println();
            }
            System.out.println();
        }*/
        
        
        // 1. Get lists and rootsteps in the format of 5
        ArrayList<UpdatedMeasure> updatedMeasureArrayList = app.calculateUpdatedMeasureList(measureList);
        ArrayList<String> step  = new ArrayList<String>();
        ArrayList<String> rootStep  = new ArrayList<String>();
        for(int x=0;x<updatedMeasureArrayList.size();x++) {
			//System.out.println("-------------Measure " + (int)(x+1) + "------------");
			UpdatedMeasure updatedMeasure = updatedMeasureArrayList.get(x);
			UpdatedHarmony updatedHarmony = updatedMeasure.getHarmony();
			//System.out.println("Rootstep: " + updatedHarmony.getRootStep());
			//System.out.println();
			ArrayList<UpdatedNote> updatedNoteList = updatedMeasure.getNoteList();
			for(int y=0; y<updatedNoteList.size(); y++) {
				UpdatedNote updatedNote = updatedNoteList.get(y);
				//System.out.println("Step: " + updatedNote.getStep());
				//System.out.println("Duration: " + updatedNote.getDuration());
				//System.out.println("Type: " + updatedNote.getType());
				//System.out.println("Beam: " + updatedNote.getBeam());
				step.add(updatedNote.getStep());
				rootStep.add(updatedHarmony.getRootStep());
			}
			//System.out.println();
		}
        
        ArrayList<String> noteRootstepString = new ArrayList<String>();
        System.out.println();
        System.out.println("Task 1: Note and Rootstep calculation");
        System.out.println("-------------------------------------");
        System.out.println("  Note          Rootstep");
        System.out.println("----------------------------");
        for(int x=0; x<step.size()-5; x++) {
        	StringBuffer temp = new StringBuffer("");
    		for(int y=x; y<x+5; y++) {
    			System.out.print(step.get(y).toLowerCase() + " ");
    			temp.append(step.get(y).toLowerCase() + " ");
    		}
    		System.out.print("  -  ");
    		temp.append("  -  ");
    		for(int y=x; y<x+5; y++) {
    			System.out.print(rootStep.get(y) + " ");
    			temp.append(rootStep.get(y) + " ");
    		}
    		System.out.println();
    		String temp2 = new String(temp);
    		noteRootstepString.add(temp2);
        }
        System.out.println();
        System.out.println();
        
        
        // 2. Mapping of frequency of n-gram NOTES and m-gram CHORDS
        System.out.println("Task 2: Note and Rootstep frequency calculation");
        System.out.println("-----------------------------------------------");
        HashMap<String,Integer> hm = new HashMap<String,Integer>();
        for(String str: noteRootstepString) {
        	if(hm.containsKey(str)) {
        		hm.put(str, hm.get(str) + 1);
        	}
        	else {
        		hm.put(str, 1);
        	}
        }
        for(Map.Entry<String, Integer> set : hm.entrySet()) {
        	System.out.println(set.getKey() + " -----------------> " + set.getValue());
        }
        
        
        // 3. Create updated XML
        System.out.println();
        System.out.println("Task 3: XML file creation");
        System.out.println("-----------------------------------------------");
        createUpdatedmusicXML(updatedMeasureArrayList);
        
    }
    
    
    /*
     * Creation of an xml file of only symbols strings which describes the melody
     */
	public static void createUpdatedmusicXML(ArrayList<UpdatedMeasure> updatedMeasureArrayList) {
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Create a root element
            Element rootElement = doc.createElement("score-partwise");
            doc.appendChild(rootElement);
            // Create an attribute for the rootElement element
            rootElement.setAttribute("version", "3.0");
            // Create an work element
            Element work = doc.createElement("work");
            rootElement.appendChild(work);
            // Create a work-title element and add it to the employee element
            Element workTitle = doc.createElement("work-title");
            workTitle.appendChild(doc.createTextNode("Golden Slippers"));
            work.appendChild(workTitle);

            // Create an identification element
            Element identification = doc.createElement("identification");
            rootElement.appendChild(identification);

            // Create a encoding element and add it to the identification element
            Element encoding = doc.createElement("encoding");
            identification.appendChild(encoding);

            // Create a encoding-date element and add it to the encoding element
            Element encodingDate = doc.createElement("encoding-date");
            encodingDate.appendChild(doc.createTextNode("2023-02-14"));
            encoding.appendChild(encodingDate);
            // Create a encoder element and add it to the encoding element
            Element encoder = doc.createElement("encoder");
            encoder.appendChild(doc.createTextNode("Josef Pavlicek"));
            encoding.appendChild(encoder);
            // Create a software element and add it to the encoding element
            Element software = doc.createElement("software");
            software.appendChild(doc.createTextNode("Sibelius 22.7.0"));
            encoding.appendChild(software);
            // Create a software element and add it to the encoding element
            Element software2 = doc.createElement("software");
            software2.appendChild(doc.createTextNode("Direct export, not from Dolet"));
            encoding.appendChild(software2);
            // Create a encodingDescription and add it to the encoding element
            Element encodingDescription = doc.createElement("encoding-description");
            encodingDescription.appendChild(doc.createTextNode("Direct export, not from Dolet"));
            encoding.appendChild(encodingDescription);
            // Create a supports element and add it to the encoding element
            Element supports = doc.createElement("supports");
            supports.setAttribute("element", "print");
            supports.setAttribute("type", "yes");
            supports.setAttribute("value", "yes");
            supports.setAttribute("attribute", "new-system");
            encoding.appendChild(supports);
            // Create a supports element and add it to the encoding element
            Element supports2 = doc.createElement("supports");
            supports2.setAttribute("element", "print");
            supports2.setAttribute("type", "yes");
            supports2.setAttribute("value", "yes");
            supports2.setAttribute("attribute", "new-page");
            encoding.appendChild(supports2);
            // Create a supports element and add it to the encoding element
            Element supports3 = doc.createElement("supports");
            supports3.setAttribute("element", "accidental");
            supports3.setAttribute("type", "yes");
            encoding.appendChild(supports3);
            // Create a supports element and add it to the encoding element
            Element supports4 = doc.createElement("supports");
            supports4.setAttribute("element", "beam");
            supports4.setAttribute("type", "yes");
            encoding.appendChild(supports4);
            // Create a supports element and add it to the encoding element
            Element supports5 = doc.createElement("supports");
            supports5.setAttribute("element", "stem");
            supports5.setAttribute("type", "yes");
            encoding.appendChild(supports5);

            // Create an defaults element
            Element defaults = doc.createElement("defaults");
            rootElement.appendChild(defaults);

            // Create a scaling element and add it to the defaults element
            Element scaling = doc.createElement("scaling");
            defaults.appendChild(scaling);
            // Create a millimeters element and add it to the scaling element
            Element millimeters = doc.createElement("millimeters");
            millimeters.appendChild(doc.createTextNode("210"));
            scaling.appendChild(millimeters);
            // Create a tenths element and add it to the scaling element
            Element tenths = doc.createElement("tenths");
            tenths.appendChild(doc.createTextNode("1400"));
            scaling.appendChild(tenths);

            // Create a pageLayout element and add it to the defaults element
            Element pageLayout = doc.createElement("page-layout");
            defaults.appendChild(pageLayout);
            // Create a pageHeight element and add it to the scaling element
            Element pageHeight = doc.createElement("page-height");
            pageHeight.appendChild(doc.createTextNode("1980"));
            pageLayout.appendChild(pageHeight);
            // Create a pageHeight element and add it to the scaling element
            Element pageWidth = doc.createElement("page-width");
            pageWidth.appendChild(doc.createTextNode("1400"));
            pageLayout.appendChild(pageWidth);
            Element pageMargins = doc.createElement("page-margins");
            pageMargins.setAttribute("type", "both");
            pageLayout.appendChild(pageMargins);

            // Create a leftMargin element and add it to the pageMargins element
            Element leftMargin = doc.createElement("left-margin");
            leftMargin.appendChild(doc.createTextNode("100"));
            pageMargins.appendChild(leftMargin);
            // Create a rightMargin element and add it to the pageMargins element
            Element rightMargin = doc.createElement("right-margin");
            rightMargin.appendChild(doc.createTextNode("100"));
            pageMargins.appendChild(rightMargin);
            // Create a leftMargin element and add it to the pageMargins element
            Element topMargin = doc.createElement("top-margin");
            topMargin.appendChild(doc.createTextNode("100"));
            pageMargins.appendChild(topMargin);
            // Create a rightMargin element and add it to the pageMargins element
            Element bottomMargin = doc.createElement("bottom-margin");
            bottomMargin.appendChild(doc.createTextNode("100"));
            pageMargins.appendChild(bottomMargin);

            // Create a pageLayout element and add it to the defaults element
            Element systemLayout = doc.createElement("system-layout");
            defaults.appendChild(systemLayout);
            // Create a systemMargins element and add it to the systemLayout element
            Element systemMargins = doc.createElement("system-margins");
            systemLayout.appendChild(systemMargins);
            // Create a leftMargin element and add it to the pageMargins element
            Element leftMargin2 = doc.createElement("left-margin");
            leftMargin2.appendChild(doc.createTextNode("21"));
            systemMargins.appendChild(leftMargin2);
            // Create a rightMargin element and add it to the pageMargins element
            Element rightMargin2 = doc.createElement("right-margin");
            rightMargin2.appendChild(doc.createTextNode("0"));
            systemMargins.appendChild(rightMargin2);
            // Create a systemDistance element and add it to the systemLayout element
            Element systemDistance = doc.createElement("system-distance");
            systemLayout.appendChild(systemDistance);

            // Create a pageLayout element and add it to the defaults element
            Element appearance = doc.createElement("appearance");
            defaults.appendChild(appearance);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth1 = doc.createElement("line-width");
            lineWidth1.setAttribute("type", "stem");
            lineWidth1.appendChild(doc.createTextNode("1.25"));
            appearance.appendChild(lineWidth1);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth2 = doc.createElement("line-width");
            lineWidth2.setAttribute("type", "beam");
            lineWidth2.appendChild(doc.createTextNode("5"));
            appearance.appendChild(lineWidth2);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth3 = doc.createElement("line-width");
            lineWidth3.setAttribute("type", "staff");
            lineWidth3.appendChild(doc.createTextNode("1.25"));
            appearance.appendChild(lineWidth3);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth4 = doc.createElement("line-width");
            lineWidth4.setAttribute("type", "light barline");
            lineWidth4.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth4);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth5 = doc.createElement("line-width");
            lineWidth5.setAttribute("type", "heavy barline");
            lineWidth5.appendChild(doc.createTextNode("5"));
            appearance.appendChild(lineWidth5);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth6 = doc.createElement("line-width");
            lineWidth6.setAttribute("type", "leger");
            lineWidth6.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth6);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth7 = doc.createElement("line-width");
            lineWidth7.setAttribute("type", "ending");
            lineWidth7.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth7);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth8 = doc.createElement("line-width");
            lineWidth8.setAttribute("type", "wedge");
            lineWidth8.appendChild(doc.createTextNode("1.25"));
            appearance.appendChild(lineWidth8);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth9 = doc.createElement("line-width");
            lineWidth9.setAttribute("type", "enclosure");
            lineWidth9.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth9);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth10 = doc.createElement("line-width");
            lineWidth10.setAttribute("type", "tuplet bracket");
            lineWidth10.appendChild(doc.createTextNode("1.25"));
            appearance.appendChild(lineWidth10);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth11 = doc.createElement("line-width");
            lineWidth11.setAttribute("type", "tuplet bracket");
            lineWidth11.appendChild(doc.createTextNode("1.25"));
            appearance.appendChild(lineWidth11);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth12 = doc.createElement("line-width");
            lineWidth12.setAttribute("type", "bracket");
            lineWidth12.appendChild(doc.createTextNode("5"));
            appearance.appendChild(lineWidth12);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth13 = doc.createElement("line-width");
            lineWidth13.setAttribute("type", "dashes");
            lineWidth13.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth13);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth14 = doc.createElement("line-width");
            lineWidth14.setAttribute("type", "extend");
            lineWidth14.appendChild(doc.createTextNode("0.9375"));
            appearance.appendChild(lineWidth14);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth15 = doc.createElement("line-width");
            lineWidth15.setAttribute("type", "octave shift");
            lineWidth15.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth15);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth16 = doc.createElement("line-width");
            lineWidth16.setAttribute("type", "pedal");
            lineWidth16.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth16);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth17 = doc.createElement("line-width");
            lineWidth17.setAttribute("type", "slur middle");
            lineWidth17.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth17);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth18 = doc.createElement("line-width");
            lineWidth18.setAttribute("type", "slur tip");
            lineWidth18.appendChild(doc.createTextNode("0.625"));
            appearance.appendChild(lineWidth18);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth19 = doc.createElement("line-width");
            lineWidth19.setAttribute("type", "tie middle");
            lineWidth19.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth19);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth20 = doc.createElement("line-width");
            lineWidth20.setAttribute("type", "tie tip");
            lineWidth20.appendChild(doc.createTextNode("1.5625"));
            appearance.appendChild(lineWidth20);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth21 = doc.createElement("note-size");
            lineWidth21.setAttribute("type", "cue");
            lineWidth21.appendChild(doc.createTextNode("75"));
            appearance.appendChild(lineWidth21);
            // Create a lineWidth element and add it to the appearance element
            Element lineWidth22 = doc.createElement("line-width");
            lineWidth22.setAttribute("type", "grace");
            lineWidth22.appendChild(doc.createTextNode("60"));
            appearance.appendChild(lineWidth22);
            // Create a musicFont element and add it to the defaults element
            Element musicFont = doc.createElement("music-font");
            musicFont.setAttribute("font-family", "Opus Std");
            musicFont.setAttribute("font-size", "17.0079");
            defaults.appendChild(musicFont);
            // Create a word-font element and add it to the defaults element
            Element wordFont = doc.createElement("word-font");
            wordFont.setAttribute("font-family", "Times New Roman");
            wordFont.setAttribute("font-size", "10.2313");
            defaults.appendChild(wordFont);
            // Create a lyric-font element and add it to the defaults element
            Element lyricFont = doc.createElement("lyric-font");
            lyricFont.setAttribute("font-family", "Calibri");
            lyricFont.setAttribute("font-size", "9.8327");
            defaults.appendChild(lyricFont);
            // Create a lyric-language element and add it to the defaults element
            Element lyricLanguage = doc.createElement("lyric-language");
            lyricLanguage.setAttribute("xml:lang", "en");
            defaults.appendChild(lyricLanguage);

            // Create an credit element
            Element credit = doc.createElement("credit");
            credit.setAttribute("page", "1");
            rootElement.appendChild(credit);
            // Create a creditWords element and add it to the credit element
            Element creditWords = doc.createElement("credit-words");
            lyricLanguage.setAttribute("default-x", "700");
            lyricLanguage.setAttribute("default-y", "155");
            lyricLanguage.setAttribute("font-family", "Times New Roman");
            lyricLanguage.setAttribute("font-style", "normal");
            lyricLanguage.setAttribute("font-size", "18.8681");
            lyricLanguage.setAttribute("font-weight", "normal");
            lyricLanguage.setAttribute("justify", "center");
            lyricLanguage.setAttribute("valign", "middle");
            lyricLanguage.appendChild(doc.createTextNode("Golden Slippers"));
            credit.appendChild(creditWords);

            // Create an credit element
            Element partList = doc.createElement("part-list");
            rootElement.appendChild(partList);
            // Create an scorePart element
            Element scorePart = doc.createElement("score-part");
            scorePart.setAttribute("id", "P1");
            partList.appendChild(scorePart);
            // Create a partName element and add it to the scorePart element
            Element partName = doc.createElement("part-name");
            partName.appendChild(doc.createTextNode("Piano"));
            scorePart.appendChild(partName);
            // Create a partNameDisplay element and add it to the scorePart element
            Element partNameDisplay = doc.createElement("part-name-display");
            scorePart.appendChild(partNameDisplay);
            // Create a display-text element and add it to the scorePart element
            Element displayText = doc.createElement("displayText");
            displayText.appendChild(doc.createTextNode("Piano"));
            partNameDisplay.appendChild(displayText);
            // Create a scoreInstrument element and add it to the scorePart element
            Element scoreInstrument = doc.createElement("score-instrument");
            scoreInstrument.setAttribute("id", "P1-I1");
            scorePart.appendChild(scoreInstrument);
            // Create a instrument-name element and add it to the scorePart element
            Element instrumentName = doc.createElement("instrument-name");
            scoreInstrument.appendChild(instrumentName);
            // Create a virtual-instrument element and add it to the scorePart element
            Element virtualInstrument = doc.createElement("virtual-instrument");
            scoreInstrument.appendChild(virtualInstrument);
            // Create a virtual-library element and add it to the virtualInstrument element
            Element virtualLibrary = doc.createElement("virtual-library");
            virtualLibrary.appendChild(doc.createTextNode("General MIDI"));
            virtualInstrument.appendChild(virtualLibrary);
            // Create a virtualName element and add it to the virtualInstrument element
            Element virtualName = doc.createElement("virtual-name");
            virtualName.appendChild(doc.createTextNode("Bright Piano"));
            virtualInstrument.appendChild(virtualName);

            // Create an part element
            Element part = doc.createElement("part");
            part.setAttribute("id", "P1");
            rootElement.appendChild(part);

            
            for(UpdatedMeasure measureData: updatedMeasureArrayList) {
            	
	            /*-----------------------First Measure------------------------------- */
	            // Create an measure element
	            Element measure = doc.createElement("measure");
	            measure.setAttribute("number", "1");
	            measure.setAttribute("width", "224");
	            part.appendChild(measure);
	
	            // Create an measure print
	            Element print = doc.createElement("print");
	            print.setAttribute("new-page", "yes");
	            measure.appendChild(print);
	            // Create an system-layout print
	            Element systemLayout2 = doc.createElement("system-layout");
	            print.appendChild(systemLayout2);
	            // Create an system-Margins print
	            Element systemMargins2 = doc.createElement("system-margins");
	            systemLayout2.appendChild(systemMargins2);
	            // Create an left-margin print
	            Element leftMargin3 = doc.createElement("left-margin");
	            leftMargin3.appendChild(doc.createTextNode("75"));
	            systemMargins2.appendChild(leftMargin3);
	            // Create an right-margin print
	            Element rightMargin3 = doc.createElement("right-margin");
	            rightMargin3.appendChild(doc.createTextNode("0"));
	            systemMargins2.appendChild(rightMargin3);
	            // Create an system-Margins print
	            Element topSystemDistance = doc.createElement("top-system-distance");
	            topSystemDistance.appendChild(doc.createTextNode("227"));
	            systemLayout2.appendChild(topSystemDistance);
	
	            // Create an measure attributes
	            Element attributes = doc.createElement("attributes");
	            measure.appendChild(attributes);
	            // Create an measure divisions
	            Element divisions = doc.createElement("divisions");
	            divisions.appendChild(doc.createTextNode("256"));
	            attributes.appendChild(divisions);
	            // Create an measure key
	            Element key = doc.createElement("key");
	            key.setAttribute("color", "#000000");
	            attributes.appendChild(key);
	            // Create an fifths key
	            Element fifths = doc.createElement("fifths");
	            fifths.appendChild(doc.createTextNode("0"));
	            key.appendChild(fifths);
	            // Create an system-Margins print
	            Element mode = doc.createElement("mode");
	            mode.appendChild(doc.createTextNode("major"));
	            key.appendChild(mode);
	
	            // Create an time key
	            Element time = doc.createElement("time");
	            time.setAttribute("color", "#000000");
	            attributes.appendChild(time);
	            // Create an beats key
	            Element beats = doc.createElement("beats");
	            beats.appendChild(doc.createTextNode("4"));
	            time.appendChild(beats);
	            // Create an beat-type key
	            Element beatType = doc.createElement("beat-type");
	            beatType.appendChild(doc.createTextNode("4"));
	            time.appendChild(beatType);
	
	            // Create an staves key
	            Element staves = doc.createElement("staves");
	            staves.appendChild(doc.createTextNode("1"));
	            attributes.appendChild(staves);
	
	            // Create an clef attributes
	            Element clef = doc.createElement("clef");
	            clef.setAttribute("number", "1");
	            clef.setAttribute("color", "#000000");
	            attributes.appendChild(clef);
	            // Create an beats clef
	            Element sign = doc.createElement("sign");
	            sign.appendChild(doc.createTextNode("G"));
	            clef.appendChild(sign);
	            // Create an beat-type clef
	            Element line = doc.createElement("line");
	            line.appendChild(doc.createTextNode("2"));
	            clef.appendChild(line);
	
	            // Create an staffDetails key
	            Element staffDetails = doc.createElement("staff-details");
	            staffDetails.setAttribute("number", "1");
	            staffDetails.setAttribute("print-object", "yes");
	            attributes.appendChild(staffDetails);
	
	            // Create an measure direction
	            Element direction = doc.createElement("direction");
	            measure.appendChild(direction);
	            // Create an measure direction-type
	            Element directionType = doc.createElement("direction-type");
	            direction.appendChild(directionType);
	            // Create an metronome directionType
	            Element metronome = doc.createElement("metronome");
	            metronome.setAttribute("default-y", "30");
	            metronome.setAttribute("color", "#000000");
	            metronome.setAttribute("font-family", "Times New Roman");
	            metronome.setAttribute("font-style", "normal");
	            metronome.setAttribute("font-size", "9.5669");
	            metronome.setAttribute("font-weight", "bold");
	            directionType.appendChild(metronome);
	            // Create an metronome beatUnit
	            Element beatUnit = doc.createElement("beat-unit");
	            beatUnit.appendChild(doc.createTextNode("quarter"));
	            metronome.appendChild(beatUnit);
	            // Create an metronome per-minute
	            Element perMinute = doc.createElement("per-minute");
	            perMinute.appendChild(doc.createTextNode("120"));
	            metronome.appendChild(perMinute);
	
	            // Create an direction voice
	            Element voice = doc.createElement("voice");
	            voice.appendChild(doc.createTextNode("1"));
	            direction.appendChild(voice);
	
	
	            // Create an harmony measure
	            Element harmony$1 = doc.createElement("harmony");
	            harmony$1.setAttribute("color", "#000000");
	            harmony$1.setAttribute("default-y", "25");
	            measure.appendChild(harmony$1);
	            // Create an root harmony
	            Element root$1 = doc.createElement("root");
	            harmony$1.appendChild(root$1);
	            // Create an root-step root
	            
	            int rootStepDigits = measureData.getHarmony().getRootStep().length();
	            String updatedKindValue = "";
	            if(rootStepDigits == 2) {
	            	char ch = measureData.getHarmony().getRootStep().charAt(1);
	            	if(ch == 'm')
	            		updatedKindValue = "minor";
	            	else if(ch == '7')
	            		updatedKindValue = "dominant";
	            	
	            	Element rootStep$1 = doc.createElement("root-step");
		            rootStep$1.appendChild(doc.createTextNode(measureData.getHarmony().getRootStep().charAt(0) + ""));
		            root$1.appendChild(rootStep$1);
		            // Create an kind harmony
		            Element kind$1 = doc.createElement("kind");
		            kind$1.appendChild(doc.createTextNode(updatedKindValue));
		            harmony$1.appendChild(kind$1);
	            }
	            else {
		            Element rootStep$1 = doc.createElement("root-step");
		            rootStep$1.appendChild(doc.createTextNode(measureData.getHarmony().getRootStep()));
		            root$1.appendChild(rootStep$1);
		            // Create an kind harmony
		            Element kind$1 = doc.createElement("kind");
		            kind$1.appendChild(doc.createTextNode("major"));
		            harmony$1.appendChild(kind$1);
	            }
	            // Create an staff harmony
	            Element staff$1 = doc.createElement("staff");
	            staff$1.appendChild(doc.createTextNode("1"));
	            harmony$1.appendChild(staff$1);
	
	            /*----------------Note 1----------------------- */
	            // Create an measure note1
	            Element note = doc.createElement("note");
	            measure.appendChild(note);
	            // Create an note pitch
	            Element pitch = doc.createElement("pitch");
	            note.appendChild(pitch);
	            // Create an pitch step
	            Element step = doc.createElement("step");
	            step.appendChild(doc.createTextNode(measureData.getNoteList().get(0).getStep().toUpperCase() + ""));
	            pitch.appendChild(step);
	            // Create an pitch step
	            Element octave = doc.createElement("octave");
	            octave.appendChild(doc.createTextNode("4"));
	            pitch.appendChild(octave);
	            // Create an note duration
	            Element duration = doc.createElement("duration");
	            duration.appendChild(doc.createTextNode("128"));
	            note.appendChild(duration);
	            // Create an note instrument
	            Element instrument = doc.createElement("instrument");
	            instrument.setAttribute("id", "P1-I1");
	            note.appendChild(instrument);
	            // Create an note voice
	            Element voice2 = doc.createElement("voice");
	            voice2.appendChild(doc.createTextNode("128"));
	            note.appendChild(voice2);
	            // Create an note type
	            Element type = doc.createElement("type");
	            type.appendChild(doc.createTextNode("quarter"));
	            note.appendChild(type);
	            // Create an note stem
	            Element stem = doc.createElement("stem");
	            stem.appendChild(doc.createTextNode("up"));
	            note.appendChild(stem);
	            // Create an note staff
	            Element staff2 = doc.createElement("staff");
	            staff2.appendChild(doc.createTextNode("up"));
	            note.appendChild(staff2);
	            // Create an metronome directionType
	            Element beam = doc.createElement("beam");
	            beam.setAttribute("number", "1");
	            note.appendChild(beam);
	           
	            /*----------------Note 2----------------------- */
	            // Create an measure note1
	            Element note2 = doc.createElement("note");
	            measure.appendChild(note2);
	            // Create an note pitch
	            Element pitch2 = doc.createElement("pitch");
	            note2.appendChild(pitch2);
	            // Create an pitch step
	            Element step2 = doc.createElement("step");
	            step2.appendChild(doc.createTextNode(measureData.getNoteList().get(1).getStep().toUpperCase() + ""));
	            pitch2.appendChild(step2);
	            // Create an pitch step
	            Element octave2 = doc.createElement("octave");
	            octave2.appendChild(doc.createTextNode("4"));
	            pitch2.appendChild(octave2);
	            // Create an note duration
	            Element duration2 = doc.createElement("duration");
	            duration2.appendChild(doc.createTextNode("128"));
	            note2.appendChild(duration2);
	            // Create an note instrument
	            Element instrument2 = doc.createElement("instrument");
	            instrument2.setAttribute("id", "P1-I1");
	            note2.appendChild(instrument2);
	            // Create an note voice
	            Element voice_2 = doc.createElement("voice");
	            voice_2.appendChild(doc.createTextNode("128"));
	            note2.appendChild(voice_2);
	            // Create an note type
	            Element type2 = doc.createElement("type");
	            type2.appendChild(doc.createTextNode("quarter"));
	            note2.appendChild(type2);
	            // Create an note stem
	            Element stem2 = doc.createElement("stem");
	            stem2.appendChild(doc.createTextNode("up"));
	            note2.appendChild(stem2);
	            // Create an note staff
	            Element staff_2 = doc.createElement("staff");
	            staff_2.appendChild(doc.createTextNode("up"));
	            note2.appendChild(staff_2);
	            // Create an metronome directionType
	            Element beam2 = doc.createElement("beam");
	            beam2.setAttribute("number", "1");
	            //beam2.appendChild(doc.createTextNode("continue"));
	            note2.appendChild(beam2);
	            /*----------------Note 3----------------------- */
	            // Create an measure note1
	            Element note3 = doc.createElement("note");
	            measure.appendChild(note3);
	            // Create an note pitch
	            Element pitch3 = doc.createElement("pitch");
	            note3.appendChild(pitch3);
	            // Create an pitch step
	            Element step3 = doc.createElement("step");
	            step3.appendChild(doc.createTextNode(measureData.getNoteList().get(2).getStep().toUpperCase() + ""));            //UNCOMMENT THIS PART when structure is correct
	            pitch3.appendChild(step3);                                                                                       // In Sibelius there are 256 rhythmic units in a quarter note, so a 4/4 bar is 1024 units long.
	            // Create an pitch step
	            Element octave3 = doc.createElement("octave");
	            octave3.appendChild(doc.createTextNode("4"));
	            pitch3.appendChild(octave3);
	            // Create an note duration
	            Element duration3 = doc.createElement("duration");
	            duration3.appendChild(doc.createTextNode("128"));
	            note3.appendChild(duration3);
	            // Create an note instrument
	            Element instrument3 = doc.createElement("instrument");
	            instrument3.setAttribute("id", "P1-I1");
	            note3.appendChild(instrument3);
	            // Create an note voice
	            Element voice_3 = doc.createElement("voice");
	            voice_3.appendChild(doc.createTextNode("128"));
	            note3.appendChild(voice_3);
	            // Create an note type
	            Element type3 = doc.createElement("type");
	            type3.appendChild(doc.createTextNode("quarter"));
	            note3.appendChild(type3);
	            // Create an note stem
	            Element stem3 = doc.createElement("stem");
	            stem3.appendChild(doc.createTextNode("up"));
	            note3.appendChild(stem3);
	            // Create an note staff
	            Element staff_3 = doc.createElement("staff");
	            staff_3.appendChild(doc.createTextNode("up"));
	            note3.appendChild(staff_3);
	            // Create an metronome directionType
	            Element beam3 = doc.createElement("beam");
	            beam3.setAttribute("number", "1");
	            //beam3.appendChild(doc.createTextNode("continue"));
	            note3.appendChild(beam3);
	            /*----------------Note 4----------------------- */
	            // Create an measure note1
	            Element note4 = doc.createElement("note");
	            measure.appendChild(note4);
	            // Create an note pitch
	            Element pitch4 = doc.createElement("pitch");
	            note4.appendChild(pitch4);
	            // Create an pitch step
	            Element step4 = doc.createElement("step");
	            step4.appendChild(doc.createTextNode(measureData.getNoteList().get(3).getStep().toUpperCase() + ""));            //UNCOMMENT THIS PART when structure is correct
	            pitch4.appendChild(step4);
	            // Create an pitch step
	            Element octave4 = doc.createElement("octave");
	            octave4.appendChild(doc.createTextNode("4"));
	            pitch4.appendChild(octave4);
	            // Create an note duration
	            Element duration4 = doc.createElement("duration");
	            duration4.appendChild(doc.createTextNode("128"));
	            note4.appendChild(duration4);
	            // Create an note instrument
	            Element instrument4 = doc.createElement("instrument");
	            instrument4.setAttribute("id", "P1-I1");
	            note4.appendChild(instrument4);
	            // Create an note voice
	            Element voice_4 = doc.createElement("voice");
	            voice_4.appendChild(doc.createTextNode("128"));
	            note4.appendChild(voice_4);
	            // Create an note type
	            Element type4 = doc.createElement("type");
	            type4.appendChild(doc.createTextNode("quarter"));
	            note4.appendChild(type4);
	            // Create an note stem
	            Element stem4 = doc.createElement("stem");
	            stem4.appendChild(doc.createTextNode("up"));
	            note4.appendChild(stem4);
	            // Create an note staff
	            Element staff_4 = doc.createElement("staff");
	            staff_4.appendChild(doc.createTextNode("up"));
	            note4.appendChild(staff_4);
	            // Create an metronome directionType
	            Element beam4 = doc.createElement("beam");
	            beam4.setAttribute("number", "1");
	            //beam4.appendChild(doc.createTextNode("end"));
	            note4.appendChild(beam4);
	
	        }
            

            // Write the content into XML file
            File xmlFile = new File("convertedMusic.xml");
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory
                    .newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(xmlFile);

            transformer.transform(source, result);
            System.out.println("XML file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	/*
	 * This function performs conversion of generated xml files "music XML" into the symbol strings that describes the melody
	 */
	public ArrayList<UpdatedMeasure> calculateUpdatedMeasureList(ArrayList<Measure> measureList) {
		ArrayList<UpdatedMeasure> updatedMeasureArrayList = new ArrayList<UpdatedMeasure>();
		
		for(int i=0; i<measureList.size(); i++) {
			Measure measure = measureList.get(i);
			
			UpdatedMeasure updatedMeasure = new UpdatedMeasure();
			UpdatedHarmony updatedHarmony = new UpdatedHarmony();
			ArrayList<UpdatedNote> updatedNoteList = new ArrayList<UpdatedNote>();
			
			//Updating the harmony
			String rootstep = measure.getHarmony().getRootStep();
			String kind = measure.getHarmony().getKind();
			
			String harmonyStr = rootstep;
			if(kind.equals("minor"))
				harmonyStr = harmonyStr.concat("m");
			else if(kind.equals("dominant"))
				harmonyStr = harmonyStr.concat("7");
			updatedHarmony.setRootStep(harmonyStr);
			
			//Updating the notestep
			ArrayList<Note> noteList = measure.getNoteList();
			int sum = 0;
			for(int j=0; j<noteList.size(); j++) 
			{	
				Note note = noteList.get(j);
				int prev = sum;
				try {
					Integer duration = Integer.parseInt(note.getDuration());
					sum = sum + duration;
				} catch (NumberFormatException nfe) {
		            System.out.println("NumberFormat Exception: invalid duration string");
		        }
				
				if(1 > prev && 1 < sum)
				{
					UpdatedNote updatedNote = new UpdatedNote();
					updatedNote.setStep(note.getStep());
					updatedNote.setDuration("256");
					updatedNote.setBeam(note.getBeam());
					updatedNote.setType("quarter");
					updatedNoteList.add(updatedNote);
				}
				if(257 > prev && 257 < sum)
				{
					UpdatedNote updatedNote = new UpdatedNote();
					updatedNote.setStep(note.getStep());
					updatedNote.setDuration("256");
					updatedNote.setBeam(note.getBeam());
					updatedNote.setType("quarter");
					updatedNoteList.add(updatedNote);
				}
				if(767 > prev && 767 < sum)
				{
					UpdatedNote updatedNote = new UpdatedNote();
					updatedNote.setStep(note.getStep());
					updatedNote.setDuration("256");
					updatedNote.setBeam(note.getBeam());
					updatedNote.setType("quarter");
					updatedNoteList.add(updatedNote);
				}
				if(1023 > prev && 1023 < sum)
				{
					UpdatedNote updatedNote = new UpdatedNote();
					updatedNote.setStep(note.getStep());
					updatedNote.setDuration("256");
					updatedNote.setBeam(note.getBeam());
					updatedNote.setType("quarter");
					updatedNoteList.add(updatedNote);
				}
				if (sum > 1024)
				{
					System.out.println("For " + (int)(i+1) + "th measure notes structure/durations are not correct.");
					return null;
				}
				
			}
			updatedMeasure.setHarmony(updatedHarmony);
			updatedMeasure.setNoteList(updatedNoteList);
			updatedMeasureArrayList.add(updatedMeasure);
		}
		
		System.out.println("------------------Temp solution-----------------------");
		for(int x=0;x<updatedMeasureArrayList.size();x++) {
			UpdatedMeasure updatedMeasure = updatedMeasureArrayList.get(x);
			UpdatedHarmony updatedHarmony = updatedMeasure.getHarmony();
			System.out.println("Rootstep: " + updatedHarmony.getRootStep());
			System.out.println();
			ArrayList<UpdatedNote> updatedNoteList = updatedMeasure.getNoteList();
			for(int y=0; y<updatedNoteList.size(); y++) {
				UpdatedNote updatedNote = updatedNoteList.get(y);
				System.out.println("Step: " + updatedNote.getStep());
				System.out.println("Duration: " + updatedNote.getDuration());
				System.out.println("Type: " + updatedNote.getType());
				//System.out.println("Beam: " + updatedNote.getBeam());
			}
			System.out.println("-------------Measure " + (int)(x+1) + " Ended------------");
			System.out.println();
			System.out.println();
			
		}
		return updatedMeasureArrayList;
	}
}

