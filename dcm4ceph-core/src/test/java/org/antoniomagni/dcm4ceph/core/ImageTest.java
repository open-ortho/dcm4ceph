package org.antoniomagni.dcm4ceph.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

import org.w3c.dom.Node;

public class ImageTest {
    public static void main(String[] args) {
        try {
            File infile = new File(
                    "/Users/afm/Desktop/Dissertacao_en/Other/Bolton_sampledata/B1893L12.jpg");
            FileImageInputStream fiis;
            fiis = new FileImageInputStream(infile);

            if (!infile.canRead()) {
                System.out.println("File not readable. Exiting.");
                System.exit(1);
            }

            Iterator iter = ImageIO.getImageReaders(fiis);
            if (!iter.hasNext()) {
                throw new IOException("Failed to detect image format");
            }
            ImageReader reader = (ImageReader) iter.next();
            reader.setInput(fiis);
            String[] formats = reader.getImageMetadata(0)
                    .getMetadataFormatNames();
            for (int i = 0; i < formats.length; i++)
                System.out.println(formats[i]);

            Node node = reader.getImageMetadata(0).getAsTree(formats[0]);
            // Node node = reader.getImageMetadata(0).get
            printChildNodeNames(node);
            Node chroma = searchChildNode(node, "JPEGvariety");
            printChildNodeNames(chroma);

            printChildNodeNames(searchChildNode(chroma,"app0JFIF"));
            
            reader.dispose();
            fiis.seek(0);

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void printChildNodeNames(Node node) {
        if (node != null) {
            System.out.println("------ Node " + node.getNodeName()
                    + " -----------");
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                System.out.println(node.getChildNodes().item(i).getLocalName());
            }
        }
    }

    private static Node searchChildNode(Node n, String name) {
        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            if (n.getChildNodes().item(i).getLocalName().equals(name))
                return n.getChildNodes().item(i);
        }
        return null;
    }
}
