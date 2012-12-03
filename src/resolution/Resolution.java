package resolution;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import base.Atome;
import base.Constante;
import base.Et;
import base.Existe;
import base.Fonction;
import base.Formule;
import base.Non;
import base.OperateurBinaire;
import base.OperateurUnaire;
import base.Ou;
import base.PourTout;
import base.Quantification;
import base.Terme;
import base.Variable;

public class Resolution {

	private Formule[] premisses;

	private Formule conclusion;

	private Formule nCcl;

	private Hashtable<Variable, Terme > substitutionToClause;
	public int changement =0;
	private ArrayList<Formule> clauses;
	public Resolution(Formule[] premisses, Formule conclusion) {
		super();
		this.premisses = premisses;
		this.conclusion = conclusion;

		negationCcl();
		skolem();

		for (int i = 0; i < premisses.length; i++) {
			System.out.println(premisses[i].toString());
		}
		System.out.println(nCcl);
		
	}

	public void negationCcl() {
		nCcl = conclusion.negation();
	}

	public void skolem() {
		for (int i = 0; i < premisses.length; i++) {

			premisses[i] = premisses[i].enleveEquImp();
			// System.out.println(premisses[i]);
			premisses[i] = premisses[i].descendreNon();
		}
		nCcl = nCcl.enleveEquImp();
		nCcl = nCcl.descendreNon();

		int nbSkol = 0;
		for (int i = 0; i < premisses.length; i++) {
			premisses[i].renommer();
			premisses[i]=premisses[i].remonterQuantificateurs();
		}
		nCcl.renommer();
		nCcl = nCcl.remonterQuantificateurs();

		for (int i = 0; i < premisses.length; i++) {
			if (premisses[i] instanceof Quantification) {
				Formule f = premisses[i];
				while (f instanceof Existe) {
					f = ((Existe) f).getF();
				}
				while (f instanceof Quantification) {
					if (f instanceof PourTout) {
						ArrayList<PourTout> aChanger = new ArrayList<PourTout>();
						Formule f2 = ((PourTout) f).getF();
						while (f2 instanceof PourTout) {
							aChanger.add((PourTout) f2);
							f2 = ((PourTout) f2).getF();
						}
						if (f2 instanceof Existe) {
							Variable[] toChange = new Variable[aChanger.size()];
							for (int j = 0; j < aChanger.size(); j++) {
								toChange[i] = aChanger.get(i).getX();
							}
							Fonction foncSkolem = new Fonction("FonctSkol"
									+ nbSkol, toChange);
							premisses[i].substituer(
									((Quantification) f2).getX(), foncSkolem);
							Formule f3 = premisses[i];
							Formule f4 = premisses[i];
							;
							while (!((Quantification) f3).getX().equals(
									((Existe) f2).getX())) {
								f4 = f3;
								f3 = ((Quantification) f3).getF();
							}
							((Existe) f4).setF(((Quantification) f3).getF());

						}
					}
					f = ((Quantification) f).getF();
				}

			}

			
			
			
			
			
			
			
			
			
			
			
			
			
			if (nCcl instanceof Quantification) {
				Formule f = nCcl;
				while (f instanceof Existe) {
					f = ((Existe) f).getF();
				}
				while (f instanceof Quantification) {
					if (f instanceof PourTout) {
						ArrayList<PourTout> aChanger = new ArrayList<PourTout>();
						Formule f2 = ((PourTout) f).getF();
						while (f2 instanceof PourTout) {
							aChanger.add((PourTout) f2);
							f2 = ((PourTout) f2).getF();
						}
						if (f2 instanceof Existe) {
							Variable[] toChange = new Variable[aChanger.size()];
							for (int j = 0; j < aChanger.size(); j++) {
								toChange[i] = aChanger.get(i).getX();
							}
							Fonction foncSkolem = new Fonction("FonctSkol"
									+ nbSkol, toChange);
							nCcl.substituer(
									((Quantification) f2).getX(), foncSkolem);
							Formule f3 = nCcl;
							Formule f4 = nCcl;
							;
							while (!((Quantification) f3).getX().equals(
									((Existe) f2).getX())) {
								f4 = f3;
								f3 = ((Quantification) f3).getF();
							}
							((Existe) f4).setF(((Quantification) f3).getF());

						}
					}
					f = ((Quantification) f).getF();
				}

			}

		}
	
	
		for (int i = 0; i < premisses.length; i++) {
			premisses[i] = premisses[i].toConjonctive();
		}
		nCcl = nCcl.toConjonctive();
		clauses = new ArrayList<Formule>();
		for (int i = 0; i < premisses.length; i++) {
			clauses.addAll(toClauses(premisses[i], clauses.size()));
		}
		clauses.addAll(toClauses(nCcl, clauses.size()));

		for(int i=0; i<clauses.size();i++){
			Formule f = clauses.get(i);
			substitutionToClause = new Hashtable<Variable, Terme>();
			renommer(f);
			Enumeration<Variable> vars = substitutionToClause.keys();
			while(vars.hasMoreElements()){
				Variable var = vars.nextElement();
				f.substituer(var, substitutionToClause.get(var));
				clauses.set(i, f);
			}
			
		}
		System.out.println(clauses);

	}
	
	
	public ArrayList<Formule> toClauses(Formule f1, int nbCst){
		ArrayList<Formule> clauses = new ArrayList<Formule>();
		
			Formule f = f1;
			while(f instanceof Quantification){
				if(f instanceof Existe){
					Constante cons = new Constante("constClause"+nbCst);
					nbCst++;
					((Existe) f).getF().substituer(((Existe) f).getX(), cons);
				}
				f = ((Quantification)f).getF();
				
			}
			if(f instanceof Ou){
				clauses.add((Ou)f);
			}else if (f instanceof Non){
				clauses.add(f);
			}else if(f instanceof Atome){
				clauses.add(f);
			}else if(f instanceof Et){
				clauses.addAll(toClauses(((Et) f).getOperande(), nbCst));
				clauses.addAll(toClauses(((Et) f).getOperandedroit(), nbCst));
				
				
			}
		return clauses;
		
	}

	
	public void renommer(Formule f ){
		if(f instanceof OperateurBinaire){
			renommer(((OperateurBinaire) f).getOperande());
			renommer(((OperateurBinaire) f).getOperandedroit());
			
		}else if(f instanceof OperateurUnaire){
			renommer(((OperateurUnaire) f).getOperande());
		}else if(f instanceof Atome){
			for(int i=0; i<((Atome) f).getArite();i++){
				renommer(((Atome) f).getOperandes());
			}
		}
		
		
	}
	
	public void renommer(Terme[] t){
		
		for(int i=0; i<t.length;i++){
			renommer(t[i]);
		}
		
		return ;
	}
	
	public void renommer(Terme t){
		if(t instanceof Fonction){
			renommer(((Fonction) t).getOperandes());
		}else if(t instanceof Variable){
			if(!t.getName().startsWith("varRename")){
				Enumeration<Variable> vars = substitutionToClause.keys();
				while(vars.hasMoreElements()){
					if(vars.nextElement().equals(((Variable)t).getName())){
						return ;
					}
				}
				substitutionToClause.put((Variable)t, new Constante("varRename"+changement));
				changement++;
			}
		}
		
		return ;
		
	}
	
	public boolean unification(){
		
		
		return false;
	}
	
	

}
