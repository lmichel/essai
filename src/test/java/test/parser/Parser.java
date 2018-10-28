/**
 * 
 */
package test.parser;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.VodmlBlockParser;

/**
 * @author michel
 * https://openclassrooms.com/fr/courses/2654406-java-et-le-xml/2686225-utiliser-xpath
 */
public class Parser {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		VodmlBlockParser treeBuilder = new VodmlBlockParser(Parser.class.getResourceAsStream("/test/xml/sample1.xml"));
		treeBuilder.parse();

	}
	
	public static void getNodes(Node n, ArrayList<Element> listElement){
		String str = new String();
		if(n instanceof Element){
			Element element = (Element)n;
			if(n.getNodeName().equals("feuille"))
				listElement.add(element);
            System.out.println(n.getNodeName());
			//Nous allons maintenant traiter les nœuds enfants du nœud en cours de traitement
			int nbChild = n.getChildNodes().getLength();
			//Nous récupérons la liste des nœuds enfants
			NodeList list = n.getChildNodes();

			//nous parcourons la liste des nœuds
			for(int i = 0; i < nbChild; i++){
				Node n2 = list.item(i);

				//si le nœud enfant est un Element, nous le traitons
				if (n2 instanceof Element){
					//appel récursif à la méthode pour le traitement du nœud et de ses enfants 
					getNodes(n2, listElement);
				}
			}
		}
	}   
}

