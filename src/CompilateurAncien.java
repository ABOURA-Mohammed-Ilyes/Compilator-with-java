import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Compilateur{
    
	private JFrame fAnalyseur;
    public static ArrayList<Character> snail = new ArrayList<>(); // Tableau contenant tout le fichier text
    public static ArrayList<String> tockenMot = new ArrayList<>();
    public static ArrayList<String> tockenId = new ArrayList<>();
	public static ArrayList<String> tockenLigne = new ArrayList<>();
    public static ArrayList<String> tockenNumEntier = new ArrayList<>();
    public static ArrayList<String> tockenNumReal = new ArrayList<>();
    public static ArrayList<String> tockenIdEntier = new ArrayList<>();
    public static ArrayList<String> tockenIdReal = new ArrayList<>();
	public static ArrayList<String> tockenIdSet = new ArrayList<>();
	public static ArrayList<String> Lexical = new ArrayList<>();
	public static ArrayList<String> Syntaxique = new ArrayList<>();
	public static ArrayList<String> Semantique = new ArrayList<>();
    public static int tockenIdIndex = 0;
    public static int snailIndex = 0;
    public static int SnlBegin=0;
    public static int SnlEnd=0;
    public static int correct;
	public static int ifCompteur;
	public static boolean analyse1 = false, analyse2=false;

    public static void OpenAndReadFile(){
		JFileChooser chooser = new JFileChooser();
		if(chooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try (FileReader f = new FileReader(chooser.getSelectedFile().getAbsolutePath())) {
				snail.clear();
				while (f.ready()) {
					char lettre = (char)f.read();
					snail.add(lettre);
				}
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
	}
    }
    
	public static void AfficheErreur ( ArrayList<String> chaine, String erreur ) {
		Syntaxique.add(chaine + " : erreur de syntaxe " + erreur);
		correct++;
	}

	public static void createLigne ( ArrayList<String> chaine ) {
		String ligne = chaine.get(0);;
		for ( int i = 1; i < chaine.size(); i++){
			ligne = ligne + " " + chaine.get(i);
		}
		tockenLigne.add(ligne);
	}

    public static boolean Check(char[] table, char lettre) {
    	
    	for (char c : table) {
    		if (lettre == c) {
    			return true;
    		}
    	}
    	
    	return false;
    	
    }
    
    public static boolean CheckRetour(String mot ) {
    	if ( mot.equals("\n") ) return true;
    	return false;
    }

    public static String GetWord(){
    	
    	char[] CharacterAcceptable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.',
    			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U',
    			'V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p',
    			'q','r','s','t','u','v','w','x','y','z','_',' ','\t','\r','\n','"',',','%','[',']',
    			'<','>','='};

        char lettre = snail.get(snailIndex);
        String mot=new String();
        if ( !Check(CharacterAcceptable, lettre) ) {
        	
        	snailIndex++;
        	mot+=lettre;
        	Lexical.add("Arret d'analyse, erreur '" + mot + "' : charactere non reconnu ");
        	return "";
        	
        }
        
        if( lettre == ',' || lettre == '"' || lettre == '[' || lettre == ']' || lettre == '<' || lettre == '>' || lettre == '=' ) {
        	mot+=lettre;
        	snailIndex++;
        	if ( lettre == '<' || lettre == '>' || lettre == '=' ) {
            	lettre = snail.get(snailIndex++);
            	if ( lettre == '=' ) mot += lettre;
        	}
        	return mot;
        }
        
        else if ( lettre == '%') {
        	if ( snail.get(snailIndex+1) != '.') {
        		snailIndex++;
        		mot += lettre;
        	}
        	else {
        		mot += lettre;
        		snailIndex++;
        		mot += snail.get(snailIndex);
        		snailIndex++;
        	}
        	
        	return mot;
        	
        }
        
        
        
        else if ( lettre == '\n' || lettre == '\r' ) {
        	mot += lettre;
        	snailIndex++;
        	return "\n";
        }
        
        else if( lettre == ' ' || lettre == '\t' ) {
        	snailIndex++;
        	return GetWord();
        }
        
        
        else {
        	while( snailIndex < snail.size()  ) {
        		lettre = snail.get(snailIndex);
        		if ( !Check( CharacterAcceptable, lettre) ) {
        			mot += lettre;
        			System.out.println("Arret d'analyse, erreur '" + mot + "' : charactere non reconnu ");
        			return "";
        		}
        		if( lettre == '\n' || lettre == ' ' || lettre == '\t' || lettre == ',' || lettre == '\r' || lettre == '"' || lettre == '%' || lettre == '[' || lettre == ']' || lettre == '<' || lettre == '>' || lettre == '=' ) {
        			
        			snailIndex++;
        			if( lettre == ',' || lettre == '"' || lettre == '\r' || lettre == '\n' || lettre == '%' || lettre == '[' || lettre == ']' || lettre == '<' || lettre == '>' || lettre == '=' ) snailIndex--;
        			return mot;
        			
        		}
        		else {
        			
        			mot += lettre;
        			snailIndex++;
        				
        			}
        		}
        	
        	return mot;
        		
        	}
        	
        }
    
    public static boolean VerifyNum(String mot) {
    	
		if ( mot.charAt(0) == '.' || mot.endsWith(".")) return false;
		char[] nombre = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' };
		int i=0,point = 0;
		
		while( i < mot.length() ) {
			if(!Check(nombre, mot.charAt(i))) return false;
			if(mot.charAt(i) == '.') point++;
			i++;
			
		}
		
		if( point == 0 ) {
			Lexical.add(mot + " : nombre entier");
        	tockenMot.add(mot);
        	tockenNumEntier.add(mot);
			return true;
		}
		else if ( point == 1 ) {
			Lexical.add(mot + " : nombre reel");
        	tockenMot.add(mot);
        	tockenNumReal.add(mot);
			return true;
		}
		else return false;
		
	}
    
    public static boolean VerifyId (String mot) {
    	
    	char[] Lettre = { 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U',
    			'V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p',
    			'q','r','s','t','u','v','w','x','y','z'};
    	
    	char[] Chiffre = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','_'};
    	
    	if (!Check(Lettre, mot.charAt(0)))	return false;
    	
    	int i=1;
    	while(i<mot.length()) {
    		if (!Check(Lettre, mot.charAt(i))) {
    			if (!Check(Chiffre, mot.charAt(i))) {
    				return false;
    			}
    			else i++;
    		}
    		else i++;
    	}
    	
    	Lexical.add(mot + " : identificateur");
    	tockenMot.add(mot);
    	tockenId.add(mot);
    	return true;
    	
    }    

    public static boolean VerifyTocken(String mot){
    	String[] mot_reserve = {  
            
            "Snl_Begin",
			"Snl_Int",
			",",
			"Set",
			"Snl_Real",
			"If",
			"<", 
			">", 
			"==", 
			"<=", 
			">=", 
            "Else",
			"from", 
			"Get", 
			"Snl_Put",
			"\"" ,
			"[" ,
			"]" ,
			"Snl_End",
			"%."
            
        };

		String[] Affichage = {
            
            "Mot cle debut de programme",
			"Mot cle pour declaration d'un entier",
			"Mot cle virgule",
			"Mot cle pour affectation d'une valeur",
			"Mot cle pour declaration d'un Real",
			"Mot cle pour une condition if", 
			"operateur de comparaison strictement inferieur",
			"operateur de comparaison strictement superieur",
			"operateur de comparaison égalité",
			"operateur de comparaison inferieur ou egale",
			"operateur de comparaison superieur ou egale",
			"Mot cle pour condition sinon", 
			"Mot cle pour affectation d'une variable",
			"Mot cle pour affectation d'une variable",
			"Mot cle pour affichage un message",
			"Caractere pour annoncer le debut ou la fin d'une chaine",
			"debut de condition",
			"Fin de condition",
		    "Mot cle Fin du programme",
		    "Mot cle pour fin d'instruction"
        
        };
		
		if (mot.equals("%")) {
			String mot2 = new String();
			do {
				mot2 = GetWord();
				mot = mot + " " + mot2;
			} while(!mot2.equals("%")) ;
			Lexical.add(mot + " : commentaire");
        	tockenMot.add(mot);
			return true;
			
		}
		
		else if ( mot.equals("Snl_Put")) {
			
			tockenMot.add(mot);
			Lexical.add(mot + " : Mot cle pour l'affichage d'un message");
			mot =  GetWord();
			if (mot.equals("\"") ) {

	        	tockenMot.add(mot);
				Lexical.add("\" : Caractere pour annoncer le debut ou la fin d'une chaine");
				mot = GetWord();
				String mot2 = new String();
				do {
					mot = mot + " " + mot2;
					mot2 = GetWord();
				} while(!mot2.equals("\"")) ;
				Lexical.add(mot + " : le message affiche");
				Lexical.add("\" : Caractere pour annoncer le debut ou la fin d'une chaine");
	        	tockenMot.add(mot);
	        	tockenMot.add(mot2);
				return true;
				
			}
			
			else {
				
				if( mot.equals("")) return false;
	        	else if(VerifyTocken(mot));
	        	else if (VerifyNum(mot));
	        	else if(VerifyId(mot));
				return true;
				
			}
			
		}
		
		

        int i = 0;
		while (i < mot_reserve.length) {
			if (mot.equals(mot_reserve[i])) {
                Lexical.add(mot + " : " + Affichage[i]);
                tockenMot.add(mot);
				return true;
			}
			
			i++;
		}
        return false;

    }
    
    public static boolean AnalyseLexical(){
    	Lexical.clear();
    	tockenMot.clear();
		tockenId.clear();
		tockenNumEntier.clear();
		tockenNumReal.clear();
        snailIndex = 0;
        while(snailIndex<snail.size()-1) {
        	
        	String mot = GetWord();
        	if( mot.equals("")) return false;
        	if ( mot == "\n" || mot == "\r") {
        		if(!tockenMot.get(tockenMot.size()-1).equals("\r") && !tockenMot.get(tockenMot.size()-1).equals("\n")) {
        			tockenMot.add(mot);
        		}
        	}
        	else if(!VerifyTocken(mot)) {
        		if(!VerifyNum(mot)) {
        			if(!VerifyId(mot)) {
        				Lexical.add("Arret d'analyse, erreur '" + mot + "' : mot non reconnu ");
        				return false;
        			}
        		}
        	}
        	
        }
        
        return true;
    }
    
    public static boolean AnalyseSyntaxique() {
    	Syntaxique.clear();
    	int tockenMotIndex=0;
		tockenLigne.clear();
		correct = 0;
		SnlBegin=0;
		SnlEnd=0;
		ArrayList<String> chaine = new ArrayList<>();
		String mot = new String();
		String temp;
    	while ( tockenMotIndex < tockenMot.size()) {
    		
    		// generation de chaques ligne
    		chaine.clear();
    		int chaineIndex = 1;
    		while( tockenMotIndex < tockenMot.size() && tockenMot.get(tockenMotIndex) != "\n" ) {
    			chaine.add(tockenMot.get(tockenMotIndex++));
    		}
    		tockenMotIndex++;
    		
    		// debut de l'analyse
    		if ( chaine.get(0).equals("Snl_Begin") ) {
    			if (chaineIndex == chaine.size()) {
					Syntaxique.add("Snl_Begin : Debut du programme");
					SnlBegin++;
					createLigne(chaine);
        		}
    			else AfficheErreur(chaine, "Snl_Begin");
	
    		}
    		
    		else if (  chaine.get(0).equals("Snl_End") ){
    			if (chaineIndex == chaine.size()) {
					Syntaxique.add("Snl_End : Fin du programme");
					SnlEnd++;
					createLigne(chaine);
        		}
    			else AfficheErreur(chaine, "Snl_End");
    			
    		}
    		
    		else if ( chaine.get(0).equals("Snl_Int")) {
				temp = "Declaration de";
    			if ( chaine.get(chaine.size()-1).equals("%.") ) {
    				while( chaineIndex < chaine.size() && tockenId.contains(chaine.get(chaineIndex++)) ) {
						temp += " [" + chaine.get(chaineIndex-1)+"]";
    					if ( chaine.get(chaineIndex).equals(",")) {
    						chaineIndex++;
    					}
    					else if ( chaineIndex == chaine.size()-1 ) {
							temp += " variables entiers";
    						Syntaxique.add(temp);
							createLigne(chaine);
    					}
    					else {
    						AfficheErreur(chaine, "Snl_Int");
    						break;
    					}
    				}
    				
    				if (chaineIndex == chaine.size()-1) AfficheErreur(chaine, "Snl_Int");
    				
    			}
    			else AfficheErreur(chaine, "Snl_Int");
    			
    		}
    		
    		else if (chaine.get(0).equals("Snl_Real")){
				temp = "Declaration de";
    			if ( chaine.get(chaine.size()-1).equals("%.") ) {
    				while( chaineIndex < chaine.size() && tockenId.contains(chaine.get(chaineIndex++)) ) {
						temp += " [" + chaine.get(chaineIndex-1)+"]";
    					if ( chaine.get(chaineIndex).equals(",")) {
    						chaineIndex++;
    					}
    					else if ( chaineIndex == chaine.size()-1 ) {
							temp += " variables reels";
    						Syntaxique.add(temp);
							createLigne(chaine);
    					}
    					else {
    						AfficheErreur(chaine, "Snl_Real");
    						break;
    					}
    				}
    				
    				if (chaineIndex == chaine.size()-1) AfficheErreur(chaine, "Snl_Real");
    				
    			}
    			else AfficheErreur(chaine, "Snl_Real");
    		}
    		
    		else if (chaine.get(0).equals("Set")){
				temp="Affectation d'une valeur entiere ";
    			if ( chaine.get(chaine.size()-1).equals("%.") && tockenId.contains(chaine.get(chaineIndex++))) {
    				if (tockenNumEntier.contains(chaine.get(chaineIndex)) || tockenNumReal.contains(chaine.get(chaineIndex))) {
    					chaineIndex++;
    					if ( chaineIndex == chaine.size()-1 ) {
							temp = temp + chaine.get(2)+ " à la variable " + chaine.get(1);
							Syntaxique.add(temp);
							createLigne(chaine);
						}
    					else AfficheErreur(chaine, "Set");
    				}
    				else AfficheErreur(chaine, "Set");
    				
    			}
    			else AfficheErreur(chaine, "Set");
    		}
    		
    		else if (chaine.get(0).equals("Get")){
				temp = "Affectation de la variable ";
    			if ( chaine.get(chaine.size()-1).equals("%.") ) {
    				if ( tockenId.contains(chaine.get(chaineIndex++)) ) {
    					if (chaine.get(chaineIndex++).equals("from")) {
    						if ( tockenId.contains(chaine.get(chaineIndex++)) ) {
    							if (chaineIndex == chaine.size()-1) {
									temp += chaine.get(3) + " à la variable " + chaine.get(1);
									Syntaxique.add(temp);
									createLigne(chaine);
								}
    							else AfficheErreur(chaine, "Get");
    						}
							else AfficheErreur(chaine, "Get");
    					}
						else AfficheErreur(chaine, "Get");
    				}
					else AfficheErreur(chaine, "Get");
    			}
				else AfficheErreur(chaine, "Get");
    		}
    		
    		else if (chaine.get(0).equals("If")){
				temp="Declaration d'une condition";
    			if(chaine.get(chaineIndex++).equals("[")) {
    				if(tockenId.contains(chaine.get(chaineIndex++))) {
    					if( chaine.get(chaineIndex).equals("<") || chaine.get(chaineIndex).equals(">") || chaine.get(chaineIndex).equals("<=") || chaine.get(chaineIndex).equals(">=") || chaine.get(chaineIndex).equals("==") ) {
    						chaineIndex++;
    						if (tockenNumEntier.contains(chaine.get(chaineIndex)) || tockenNumReal.contains(chaine.get(chaineIndex)) || tockenId.contains(chaine.get(chaineIndex))) {
    							chaineIndex++;
    							if(chaine.get(chaineIndex++).equals("]")) {
    								if (chaineIndex == chaine.size()) {
										Syntaxique.add(temp);
										createLigne(chaine);
									}
    								else AfficheErreur(chaine, "If");
    							}
								else AfficheErreur(chaine, "If");
    						}
							else AfficheErreur(chaine, "If");
    					}
						else AfficheErreur(chaine, "If");
    				}
					else AfficheErreur(chaine, "If");
    			}
				else AfficheErreur(chaine, "If");
    		}
    		
    		else if (chaine.get(0).equals("Else")){
    			temp="Declaration du Else";
				if (chaineIndex == chaine.size()) {
					Syntaxique.add(temp);
					createLigne(chaine);
				}
    			else AfficheErreur(chaine, "Else");
    		}
    		
    		else if (chaine.get(0).equals("Snl_Put")){
    			temp="Afiichage ";
				if ( chaine.get(chaine.size()-1).equals("%.") ) {
    				if (chaine.get(chaineIndex++).equals("\"") && chaine.get(++chaineIndex).equals("\"")) {
						temp+=" d'un message à l'ecran";
    					chaineIndex++;
    					if(chaineIndex == chaine.size()-1) {
    						Syntaxique.add(temp);
							createLigne(chaine);
    					}
    					else AfficheErreur(chaine, "Snl_Put");
    				}
    				else if (tockenId.contains(chaine.get(--chaineIndex))){
						temp+=" de la variable "+chaine.get(1);
    					chaineIndex++;
    					if(chaineIndex == chaine.size()-1 ) {
							Syntaxique.add(temp);
							createLigne(chaine);
						}
    					else AfficheErreur(chaine, "Snl_Put");
    					
    				}
    				else AfficheErreur(chaine, "Snl_Put");
    			}
    			else AfficheErreur(chaine, "Snl_Put");
    		}
    		
    		else if (chaine.get(0).startsWith("%" ) && chaine.get(0).endsWith("%" )) {
				Syntaxique.add("Un commentaire");
				chaine.clear();
				chaine.add("commentaire");
				createLigne(chaine);
    		}
    		
    		else {
				AfficheErreur(chaine, "");
				correct++;
			}

    	}
		if ( correct == 0 ) return true;
		else  return false ;
    	
    }
    
    public static void AnalyseSemantique() {
    	Semantique.clear();
		tockenIdEntier.clear();
		tockenIdReal.clear();
		tockenIdSet.clear();
		if( SnlBegin == 1 && SnlEnd == 1 && tockenLigne.get(0).equals("Snl_Begin") && tockenLigne.get(tockenLigne.size()-1).equals("Snl_End")){
			
			Semantique.add("ligne 1 et ligne " + (tockenLigne.size()) +" : Semantique de debut et fin de programme respecté");
			int tockenLigneIndex = 1;
			boolean cond;
			ifCompteur=0;
			while ( tockenLigneIndex < tockenLigne.size()-1){
				cond = true;
				String[] mot = tockenLigne.get(tockenLigneIndex).split(" ");
				if ( mot[0].equals("Snl_Int") ) {
					for ( int i = 1 ; i < mot.length ; i+=2) {
						if ( !tockenIdEntier.contains(mot[i]) && !tockenIdReal.contains(mot[i]) ) tockenIdEntier.add(mot[i]);
						else {
							Semantique.add("***ligne " + (tockenLigneIndex+1) +" : [ La variable : " + mot[i] + " a deja ete declare ]");
							cond=false;
						}
					}
					if (cond) Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Snl_Int est correcte");
					else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Snl_Int est incorrecte");
				}

				else if ( mot[0].equals("Snl_Real") ){
					for ( int i = 1 ; i < mot.length ; i+=2) {
						if ( !tockenIdEntier.contains(mot[i]) && !tockenIdReal.contains(mot[i]) ) tockenIdReal.add(mot[i]);
						else {
							Semantique.add("ligne " + (tockenLigneIndex+1) +" : La variable : " + mot[i] + " a deja ete declare");
							cond=false;
						}
					}
					if (cond) Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Snl_Real est correcte");
					else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Snl_Real est incorrecte");
				}

				else if ( mot[0].equals("Set") ){
					if ( tockenIdEntier.contains(mot[1]) ) {
						if ( tockenNumEntier.contains(mot[2])){
							Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Set est correcte");
							tockenIdSet.add(mot[1]);
						}
						else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Set est incorrecte [ la variable "+ mot[1] +"  est de type entiere et ne peut pas recevoir un nombre reel ]");
					}
					else if ( tockenIdReal.contains(mot[1]) ) {
						Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Set est correcte");
						tockenIdSet.add(mot[1]);
					}
					else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Set est incorrecte [ la variable "+ mot[1] +" n'a pas ete declare]");
				}
				
				else if ( mot[0].equals("Get") ){
					if( tockenIdSet.contains(mot[3])){
						if ( tockenIdEntier.contains(mot[1]) ){
							if ( tockenIdEntier.contains(mot[3]) ) {
								Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Get est correcte");
							}
							else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Get est incorrecte [ les variable " + mot[1] +" et " + mot[3] + " ne sont pas du meme type]");
						}
						else if ( tockenIdReal.contains(mot[1]) ){
							if ( tockenIdEntier.contains(mot[3]) ) {
								Semantique.add("ligne " + (tockenLigneIndex+1) + " : La semantique Get est correcte");
							}
							else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Get est incorrecte [ les variable " + mot[1] +" et " + mot[3] + " ne sont pas du meme type]");
						}
					}
					else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Get est incorrecte [ la variable " + mot[3] +" ne contient aucune valeur a affecter ]");
				}

				else if ( mot[0].equals("If") ){
					if ( tockenIdSet.contains(mot[2]) || tockenNumEntier.contains(mot[2]) || tockenNumReal.contains(mot[2])) {
						if ( tockenIdSet.contains(mot[4]) || tockenNumEntier.contains(mot[4]) || tockenNumReal.contains(mot[4])) {
							Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique If est correcte");
							ifCompteur++;
						}
						else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique If est incorrecte [ la variable " + mot[3] +" ne contient aucune valeur a affecter ]");
				}
				else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique If est incorrecte [ la variable " + mot[1] +" ne contient aucune valeur a affecter ]");
				}

				else if ( mot[0].equals("Else") ){
					if ( ifCompteur > 0 ) {
						Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Else est correcte");
						ifCompteur--;
					} 
					else Semantique.add("ligne " + (tockenLigneIndex+1) +" : La semantique Else est incorrecte [ le Else n'est affecte n'a aucun If qui le precede ]");
				}

				else if ( mot[0].equals("Snl_Put") ){
					if (mot[1].equals("\"")) Semantique.add("ligne " + (tockenLigneIndex+1) + " : La semantique Snl_Put est correcte");
					else if ( tockenIdSet.contains(mot[1])) Semantique.add("ligne " + (tockenLigneIndex+1) + " : La semantique Snl_Put est correcte");
					else Semantique.add("ligne " + (tockenLigneIndex+1) + " : La semantique Snl_Put est incorrecte [ la variable " + mot[1] + " ne contient aucune valeur ]");
				}

				tockenLigneIndex++;
			}

		}

		else Semantique.add("erreur snl_begin/snl_end");

    }
    
    public static void main(String[] args){
    	//snail.clear();
        //OpenAndReadFile("snail.txt");
        //AnalyseLexical();
        //	AnalyseSyntaxique();
		//		AnalyseSemantique();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Compilateur window = new Compilateur();
					window.fAnalyseur.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public  Compilateur (){
		
			//code de la fenetre
		fAnalyseur = new JFrame();
		fAnalyseur.setBounds(100, 120, 450, 350);
		fAnalyseur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fAnalyseur.setResizable(false);
		fAnalyseur.setTitle("Compilateur");
		fAnalyseur.getContentPane().setBackground(new Color(15,63,58));
		fAnalyseur.getContentPane().setLayout(null);
		fAnalyseur.setLocationRelativeTo(null); 
		fAnalyseur.setSize(850,650);
		fAnalyseur.setBounds(100, 100, 778, 687);
		Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(227,242,231));
		panel.setBounds(0, 180, 764, 493);
		fAnalyseur.getContentPane().add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(0, 0, 692, 340);
		textArea.setForeground(Color.BLACK);
		textArea.setBackground(new Color(227,242,231));
		textArea.setFont(new Font("Perpetua", Font.BOLD, 16));
		scrollPane.setBounds(0, 0, 762, 470);
		scrollPane.getViewport().add(textArea);
		panel.add(scrollPane);

			// semantique
		JButton btnAsmantique = new JButton("semantique");
		btnAsmantique.setBounds(512, 100, 236, 59);
		btnAsmantique.setForeground(Color.black);
		btnAsmantique.setBackground(new Color(137,165,162));
		btnAsmantique.setFont(new Font("Roboto", Font.BOLD, 17));
		btnAsmantique.setCursor(cursor);
		fAnalyseur.getContentPane().add(btnAsmantique);
		btnAsmantique.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				if ( !analyse2 ) textArea.setText("Erreur, l'analyse syntaxique n'a pas encore été faite ou n'a pas pu se finir");
				else {
					AnalyseSemantique();
					for ( int i = 0; i < Semantique.size() ; i++ ) textArea.setText(textArea.getText()+Semantique.get(i)+"\n");
				}
			}
		});

			//syntaxique
		JButton btnAsyntaxique = new JButton("Syntaxique");
		btnAsyntaxique.setBounds(265, 100, 236, 59);
		btnAsyntaxique.setForeground(Color.BLACK);
		btnAsyntaxique.setBackground(new Color(137,165,162));
		btnAsyntaxique.setFont(new Font("Roboto", Font.BOLD, 17));
		btnAsyntaxique.setCursor(cursor);
		fAnalyseur.getContentPane().add(btnAsyntaxique);
			//code syntaxique
		btnAsyntaxique.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				if ( !analyse1 ) textArea.setText("Erreur, l'analyse lexical n'a pas encore été faite ou n'a pas pu se finir");
				else {
					analyse2=AnalyseSyntaxique();
					for ( int i = 0; i < Syntaxique.size() ; i++ ) textArea.setText(textArea.getText()+Syntaxique.get(i)+"\n");
				}
			}
		});

			//lexical
		JButton btnAlexicale = new JButton("Lexicale");
		btnAlexicale.setBounds(18, 100, 236, 59);
		btnAlexicale.setForeground(Color.BLACK);
		btnAlexicale.setBackground(new Color(137,165,162));
		btnAlexicale.setFont(new Font("Roboto", Font.BOLD, 17));
		btnAlexicale.setCursor(cursor);
		fAnalyseur.getContentPane().add(btnAlexicale);
			//code lexical
		btnAlexicale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				if ( snail.size()==0 ) textArea.setText("Erreur lors du chargement de votre fichier, veuillez reessayer");
				else {
					analyse1 = AnalyseLexical();
					for ( int i = 0; i < Lexical.size() ; i++ ) textArea.setText(textArea.getText()+Lexical.get(i)+"\n");
				}
			}
		});

			//importer
		JButton btnNewButton = new JButton("importer");
		btnNewButton.setBounds(265, 10, 236, 59);
		btnNewButton.setForeground(Color.BLACK);
		btnNewButton.setBackground(new Color(137,165,162));
		btnNewButton.setFont(new Font("Roboto", Font.BOLD, 17));
		btnNewButton.setCursor(cursor);
		fAnalyseur.getContentPane().add(btnNewButton);
			//code importation
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				OpenAndReadFile();
				for ( int i = 0 ; i < snail.size() ; i++ ) textArea.setText(textArea.getText()+snail.get(i));
			}  
		});
	}

}