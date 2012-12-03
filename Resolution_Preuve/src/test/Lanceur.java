package test;

import resolution.Resolution;
import base.Atome;
import base.Formule;
import base.Implique;
import base.Non;
import base.PourTout;
import base.Variable;

public class Lanceur {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Variable x = new Variable("x");
		Atome al1 = new Atome("al", x);
		Non nAl = new Non(al1);
		Atome avx1 = new Atome("av", x);
		Implique impl = new Implique(avx1, nAl);
		
		PourTout p1 = new PourTout(x, impl);
		
		Atome oeufx1 = new Atome("oeuf", x);
		Atome avx2 = new Atome("av", x);
		Implique impl2 = new Implique(oeufx1, avx2);
		
		PourTout p2 = new PourTout(x, impl2);
		Atome oeufx2 = new Atome("oeuf", x);
		
		
		Atome al2 = new Atome("al", x);
		Non noeufx = new Non(oeufx2);
		Implique impl3 = new Implique(al2, noeufx);
		PourTout ccl = new PourTout(x, impl3);
		
		
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(ccl);
		Formule[] premisses = new Formule[2];
		premisses[0] = p1;
		premisses[1]= p2;
		Resolution resol = new Resolution(premisses, ccl);
		
	}

}
