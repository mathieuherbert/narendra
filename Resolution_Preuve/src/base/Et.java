package base;
/**
 *  Copyright (c) 1999-2011, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by IntelliJ IDEA.
 * User: njussien
 * Date: 30/11/12
 * Time: 10:37
 */
public class Et extends OperateurBinaire {
    public Et(Formule a, Formule b) {super(a,b);}

	@Override
	public Formule negation() {
		return new Ou(this.operande.negation(), this.operandedroit.negation());
	}

	@Override
	public Formule descendreNon() {
		return new Et(operande.descendreNon(),operandedroit.descendreNon());
	}
	
	public String toString(){
		return "("+operande.toString()+" ET "+operandedroit.toString()+") ";
	}

	@Override
	public Formule enleveEquImp() {
		// TODO Auto-generated method stub
		return new Et(operande.enleveEquImp(), operandedroit.enleveEquImp());
	}
	
	public void renommer(){
		Formule f = operande;
		int nbChang =0;
		while(f instanceof Quantification){
			if(f instanceof Existe){
				Variable aSubstituer = new Variable(((Existe) f).getX().nom+nbChang);
				nbChang++;
				Formule f2 = operande;
				while(f2 instanceof Quantification){
					if(((Quantification)f2).getX().equals(((Existe) f).getX())){
						Variable avant = ((Quantification)f2).getX();
						((Quantification)f2).setX(aSubstituer);
						((Quantification)f2).substituer(avant, aSubstituer);
					}
					f2 = ((PourTout)f2).getF();
				}
				f2.renommer();
			}
		}
		operande.renommer();
		operandedroit.renommer();
	}

	@Override
	public Formule remonterQuantificateurs() {
		
		operande = operande.remonterQuantificateurs();
		operandedroit = operandedroit.remonterQuantificateurs();
		if(operande instanceof Quantification){
			if(operande instanceof Existe){
				return new Existe(((Quantification)operande).x, new Et(((Quantification)operande).getF().remonterQuantificateurs(),operande.remonterQuantificateurs()));
			}else{
				return new PourTout(((Quantification)operande).x, new Et(((Quantification)operande).getF().remonterQuantificateurs(),operande.remonterQuantificateurs()));
			}
		}else if(operandedroit instanceof Quantification){
			if(operandedroit instanceof Existe){
				return new Existe(((Quantification)operandedroit).x, new Et(((Quantification)operande).getF().remonterQuantificateurs(),operande.remonterQuantificateurs()));
			}else{
				return new PourTout(((Quantification)operandedroit).x, new Et(((Quantification)operande).getF().remonterQuantificateurs(),operande.remonterQuantificateurs()));
			}
		}else{
			return this;
		}
	}

	@Override
	public Formule toConjonctive() {
		// TODO Auto-generated method stub
		return new Et(operande.toConjonctive(), operandedroit.toConjonctive());
	}
	
	

}
