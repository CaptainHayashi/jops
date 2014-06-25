package jops;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A FileProbe loads a file using ffprobe, extracts metadata for it, and makes
 * sure it exists.
 */
public class FileProbe {
    /**
     * Probes a file.
     * 
     * This is equivalent to new FileProbe(inFile, inModel).run().
     * @param inFile  The File to probe.
     * @param inModel The Model to update with the probed metadata.
     * @return True if the file exists; false otherwise.
     */
    public static boolean probe(File inFile, Model inModel) {
	return new FileProbe(inFile, inModel).run();
    }

    private File file;
    private Model model;
    
    /**
     * Constructs a FileProbe
     * @param inFile  The File to probe.
     * @param inModel The Model to update with the probed metadata.
     */
    public FileProbe(File inFile, Model inModel) {
	this.file = inFile;
	this.model = inModel;
    }
    
    public boolean run() {
	boolean success = false;
	
	Process probe = null;
	
	try {
	    probe = new ProcessBuilder("ffprobe", "-i", this.file.getAbsolutePath(), "-show_entries", "format=duration:format_tags=title,artist,album", "-v", "quiet", "-of", "xml").start();
	} catch (IOException e) {
	    // Ignore - will be treated as failure
	}
	
	if (probe != null) {
	    success = parseXMLFromInput(probe.getInputStream());
	}
	return success;
    }
    
    private boolean parseXMLFromInput(InputStream is) {
	boolean success = false;

	DocumentBuilder db = null;    
	try {
	    db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	} catch (ParserConfigurationException e) {
	    // Ignore - will be treated as failure
	}
	
	if (db != null) {
	    success = parseXMLFromInputWithDB(is, db);
	}
	
	return success;
    }
    
    private boolean parseXMLFromInputWithDB(InputStream is, DocumentBuilder db) {
	boolean success = false;
	
	Document doc = null;
	try {
	    doc = db.parse(is);
	} catch (SAXException | IOException e) {
	    // Ignore - will be treated as failure
	}
	
	if (doc != null) {
	    success = parseDocument(doc);
	}
	
	return success;
    }
    
    private boolean parseDocument(Document doc) {
	boolean success = false;
	
	Element root = doc.getDocumentElement();
	root.normalize();
	
	NodeList nl = doc.getElementsByTagName("format");
	if (nl.getLength() == 1) {
	    // We have a format - ffprobe managed to parse this file.
	    success = true;
	    
	    Node format = nl.item(0);
	    NamedNodeMap formatAttrs = format.getAttributes();
	    
	    parseDuration(formatAttrs);
	    
	    NodeList tags = format.getChildNodes();
	    for (int i = 0; i < tags.getLength(); i++) {
		Node n = tags.item(i);
		if (n.getNodeName().equals("tag")) {
		    parseTag(n);
		} else {
		    System.err.println("Unexpected child of format: " + n.getNodeName());
		}
	    }
	}
	
	return success;
    }
    
    private void parseTag(Node n) {
	NamedNodeMap nAttrs = n.getAttributes();
	
	String key = nAttrs.getNamedItem("key").getNodeValue();
	String value = nAttrs.getNamedItem("value").getNodeValue();
	
	switch(key) {
	case "title":
	    this.model.setSongTitle(value);
	    break;
	case "artist":
	    this.model.setSongArtist(value);
	    break;
	case "album":
	    this.model.setSongAlbum(value);
	    break;
	default:
	    System.err.println("Unknown song metadata key ignored: " + key);
	}
    }

    private void parseDuration(NamedNodeMap formatAttrs) {
	Node durationNode = formatAttrs.getNamedItem("duration");
	String durationString = durationNode.getNodeValue();
	double durationSeconds = Double.parseDouble(durationString);
	this.model.setTotalDuration((long) (durationSeconds * 1000000));
    }
}
