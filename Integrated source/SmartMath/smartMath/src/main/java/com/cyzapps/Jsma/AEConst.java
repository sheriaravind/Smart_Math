package com.cyzapps.Jsma;

import java.util.LinkedList;

import com.cyzapps.Jfcalc.MFPNumeric;
import com.cyzapps.Jmfp.VariableOperator;
import com.cyzapps.Jfcalc.BaseData;
import com.cyzapps.Jfcalc.ErrProcessor;

public class AEConst extends AbstractExpr {
    
    public String mstrKnownVarName = "";
	private BaseData.DataClass mdatumValue = new BaseData.DataClass();    // declare it to private is the best way to avoid change outof scope.
	
	public AEConst() {
		initAbstractExpr();
	}
	
	public AEConst(BaseData.DataClass datum) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		setAEConst(datum);
	}
	
	public AEConst(String strName, LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces) throws SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		setAEConst(strName, lVarNameSpaces);
	}

	public AEConst(AbstractExpr aexprOrigin) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		copy(aexprOrigin);  // no need to deep copy because data is a constant.
	}

	@Override
	protected void initAbstractExpr() {
		menumAEType = ABSTRACTEXPRTYPES.ABSTRACTEXPR_VALUE;
		mdatumValue = new BaseData.DataClass();
	}

	@Override
	public void validateAbstractExpr() throws SMErrProcessor.JSmartMathErrException {
		if (menumAEType != ABSTRACTEXPRTYPES.ABSTRACTEXPR_VALUE && menumAEType != ABSTRACTEXPRTYPES.ABSTRACTEXPR_DATAREFVALUE
				&& menumAEType != ABSTRACTEXPRTYPES.ABSTRACTEXPR_KNOWNVAR)	{
			throw new SMErrProcessor.JSmartMathErrException(SMErrProcessor.ERRORTYPES.ERROR_INCORRECT_ABSTRACTEXPR_TYPE);
		}
	}
	
	private void setAEConst(BaseData.DataClass datum) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		if (datum == null)	{
			mdatumValue = new BaseData.DataClass();
			menumAEType = ABSTRACTEXPRTYPES.ABSTRACTEXPR_VALUE;
		} else if (datum.getDataType() == BaseData.DATATYPES.DATUM_REF_DATA)	{
			mdatumValue = new BaseData.DataClass();
			mdatumValue = datum;    // no need to deep copy i.e. copyTypeValueDeep(datum). We need to ensure that datum will not change.
			menumAEType = ABSTRACTEXPRTYPES.ABSTRACTEXPR_DATAREFVALUE;
		} else	{
			mdatumValue = new BaseData.DataClass();
			mdatumValue = datum;    // no need to deep copy i.e. copyTypeValueDeep(datum). We need to ensure that datum will not change.
			menumAEType = ABSTRACTEXPRTYPES.ABSTRACTEXPR_VALUE;
		}
		validateAbstractExpr();
	}

	private void setAEConst(String strName, LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces)
			throws SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		BaseData.DataClass datum = VariableOperator.lookUpSpaces4Value(strName, lVarNameSpaces);
		if (datum == null)	{
			// the variable value is not found
			throw new SMErrProcessor.JSmartMathErrException(SMErrProcessor.ERRORTYPES.ERROR_VARIABLE_UNDECLARED);
		}
		mdatumValue = new BaseData.DataClass();
		mdatumValue.copyTypeValueDeep(datum);   // need to deep copy here because value in lVarNameSpaces may change in a function.
		mstrKnownVarName = strName;
		menumAEType = ABSTRACTEXPRTYPES.ABSTRACTEXPR_KNOWNVAR;
		validateAbstractExpr();
	}
    
    public BaseData.DataClass getDataClassCopy() throws ErrProcessor.JFCALCExpErrException {
        BaseData.DataClass datumReturn = new BaseData.DataClass();
        datumReturn.copyTypeValueDeep(mdatumValue);
        return datumReturn;
    }
    
    public BaseData.DataClass getDataClassRef() {
        return mdatumValue; // this means returned datum value will not be changed.
    }
    
    // this function returns a deep copy of mdatumValue if it is a simple data type (bool, int, double and complex).
    // if it is a string or matrix, returns a reference.
    public BaseData.DataClass getDataClass() throws ErrProcessor.JFCALCExpErrException {
        if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_BOOLEAN || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_INTEGER
                || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_DOUBLE || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_DOUBLE) {
            BaseData.DataClass datumReturn = new BaseData.DataClass();
            datumReturn.copyTypeValueDeep(mdatumValue);
            return datumReturn;
        } else {
            return mdatumValue;
        }
    }

	@Override
	protected void copy(AbstractExpr aexprOrigin) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		((AEConst)aexprOrigin).validateAbstractExpr();

		super.copy(aexprOrigin);
		mdatumValue = new BaseData.DataClass();
		if (((AEConst)aexprOrigin).mdatumValue != null)	{
			mdatumValue = ((AEConst)aexprOrigin).mdatumValue;   // no need to deep copy coz AbstractExpr is immutable.
		}
	}

	@Override
	protected void copyDeep(AbstractExpr aexprOrigin) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		((AEConst)aexprOrigin).validateAbstractExpr();

		super.copyDeep(aexprOrigin);
		mdatumValue = new BaseData.DataClass();
		if (((AEConst)aexprOrigin).mdatumValue != null)	{
            // do not deep copy because AEConst is immutable means AEConst always refer to the same dataclass although dataclass itself can change..
            // another reason do not deep copy is, if deep copy datumValue, and if datumValue is DATUM_ABSTRACT_EXPR type, and DATUM_ABSTRACT_EXPR
            // type refer to this AEConst, then we will deep copy infinitely.
			mdatumValue = ((AEConst)aexprOrigin).mdatumValue;
		}
	}
	
	@Override
	public AbstractExpr cloneSelf() throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		AbstractExpr aeReturn = new AEConst();
		aeReturn.copyDeep(this);
		return aeReturn;
	}
	
	@Override
	public int[] recalcAExprDim(boolean bUnknownAsSingle) throws SMErrProcessor.JSmartMathErrException,
            ErrProcessor.JFCALCExpErrException {
		validateAbstractExpr();

		return mdatumValue.recalcDataArraySize();
	}

	@Override
	public boolean isEqual(AbstractExpr aexpr) throws ErrProcessor.JFCALCExpErrException {
		if (menumAEType != aexpr.menumAEType)	{
			return false;	// this has been able to confirm that aexpr must be a constant.
		} else if (menumAEType != ABSTRACTEXPRTYPES.ABSTRACTEXPR_KNOWNVAR)	{
			return mdatumValue.isEqual(((AEConst)aexpr).mdatumValue);
		} else {
			return mdatumValue.isEqual(((AEConst)aexpr).mdatumValue)
				&& mstrKnownVarName.equalsIgnoreCase(((AEConst)aexpr).mstrKnownVarName);
		}
	}

    @Override
    public boolean isPatternMatch(AbstractExpr aePattern,
                                LinkedList<PatternManager.PatternExprUnitMap> listpeuMapPseudoFuncs,
                                LinkedList<PatternManager.PatternExprUnitMap> listpeuMapPseudoConsts,
                                LinkedList<PatternManager.PatternExprUnitMap> listpeuMapUnknowns,
                                boolean bAllowConversion) throws ErrProcessor.JFCALCExpErrException {
        /* do not call isPatternDegrade function because we don't allow const abstractexpr degrade-matchs a pattern. Otherwise will be a lot of troubles.*/
        if (aePattern.menumAEType == ABSTRACTEXPRTYPES.ABSTRACTEXPR_VARIABLE)   {
            // unknown variable
            for (int idx = 0; idx < listpeuMapUnknowns.size(); idx ++)  {
                if (listpeuMapUnknowns.get(idx).maePatternUnit.isEqual(aePattern))    {
                    if (isEqual(listpeuMapUnknowns.get(idx).maeExprUnit))   {
                        // this unknown variable has been mapped to an expression and the expression is the same as this
                        return true;
                    } else  {
                        // this unknown variable has been mapped to an expression but the expression is not the same as this
                        return false;
                    }
                }
            }
            // the aePattern is an unknown variable and it hasn't been mapped to some expressions before.
            PatternManager.PatternExprUnitMap peuMap = new PatternManager.PatternExprUnitMap(this, aePattern);
            listpeuMapUnknowns.add(peuMap);
            return true;
        }
        if (aePattern instanceof AEConst)   {
            if (((AEConst)aePattern).mdatumValue.isEqual(mdatumValue))  {
                return true;
            }
        } else if (aePattern.menumAEType == ABSTRACTEXPRTYPES.ABSTRACTEXPR_PSEUDOCONST) {
            // pseudo constant can pattern any constant.
            for (int idx = 0; idx < listpeuMapPseudoConsts.size(); idx ++)  {
                if (listpeuMapPseudoConsts.get(idx).maePatternUnit.isEqual(aePattern))    {
                    if (isEqual(listpeuMapPseudoConsts.get(idx).maeExprUnit))   {
                        // this pseudo-const variable has been mapped to a const and the const is the same as this
                        return true;
                    } else  {
                        // this pseudo-const variable has been mapped to a const and the const is not the same as this
                        return false;
                    }
                }
            }
            // the aePattern is an unknown variable and it hasn't been mapped to some expressions before.
            PatternManager.PatternExprUnitMap peuMap = new PatternManager.PatternExprUnitMap(this, aePattern);
            listpeuMapPseudoConsts.add(peuMap);
            return true;
        }
        
        return false;
    }
    
	@Override
	public boolean isKnownValOrPseudo() {
		return true;
	}
	
	// note that the return list should not be changed.
	@Override
	public LinkedList<AbstractExpr> getListOfChildren()	{
		LinkedList<AbstractExpr> listChildren = new LinkedList<AbstractExpr>();
		return listChildren;
	}
    
    @Override
    public AbstractExpr copySetListOfChildren(LinkedList<AbstractExpr> listChildren)  throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
        if (listChildren != null && listChildren.size() != 0) {
            throw new SMErrProcessor.JSmartMathErrException(SMErrProcessor.ERRORTYPES.ERROR_INVALID_ABSTRACTEXPR);
        }
        return this;    // AEConst does not have any child.
    }
	
	// this function replaces children who equal aeFrom to aeTo and
	// returns the number of children that are replaced.
	@Override
	public AbstractExpr replaceChildren(LinkedList<PatternManager.PatternExprUnitMap> listFromToMap, boolean bExpr2Pattern, LinkedList<AbstractExpr> listReplacedChildren) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		return this;
	}

	@Override
	public AbstractExpr distributeAExpr(SimplifyParams simplifyParams) throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		validateAbstractExpr();
		return this;
	}
	
    // avoid to do any overhead work.
    // avoid to do any overhead work.
	@Override
	public BaseData.DataClass evaluateAExprQuick(
			LinkedList<UnknownVarOperator.UnknownVariable> lUnknownVars,
			LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces)
			throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		validateAbstractExpr(); // still needs to do some basic validation.
        
        return getDataClass();          // need to do deep copy if not a matrix.
    }

    // avoid to do any overhead work.
	@Override
	public AbstractExpr evaluateAExpr(
			LinkedList<UnknownVarOperator.UnknownVariable> lUnknownVars,
			LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces)
			throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		validateAbstractExpr(); // still needs to do some basic validation.
        
        return this;        
    }
    
	@Override
	public AbstractExpr simplifyAExpr(
			LinkedList<UnknownVarOperator.UnknownVariable> lUnknownVars,
			LinkedList<LinkedList<VariableOperator.Variable>> lVarNameSpaces,
            SimplifyParams simplifyParams)
			throws InterruptedException, SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		validateAbstractExpr();
		// if the type was AEConst known var, it is now changed to AEConst value or AEConst dataref value.
        BaseData.DataClass datumValue = mdatumValue;
        if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR && !simplifyParams.mbNotSimplifyAExprDatum) {
            AbstractExpr aexpr = mdatumValue.getAExpr();
            aexpr = aexpr.simplifyAExpr(lUnknownVars, lVarNameSpaces, simplifyParams);
            if (aexpr instanceof AEConst) {
                datumValue = ((AEConst)aexpr).mdatumValue;
            } else {
                datumValue = new BaseData.DataClass(aexpr); // cannot simply use mdatumValue.setAexpr(aexpr) otherwise this is changed
            }
        }
        AEConst aexprReturn = new AEConst(datumValue);
		return aexprReturn;
	}

    @Override
    public boolean needBracketsWhenToStr(ABSTRACTEXPRTYPES enumAET, int nLeftOrRight) throws ErrProcessor.JFCALCExpErrException {
        // null means no opt, nLeftOrRight == -1 means on left, == 0 means on both, == 1 means on right
        switch (menumAEType)    {
            case ABSTRACTEXPR_VALUE:
            case ABSTRACTEXPR_KNOWNVAR:
                boolean bHasPosNegOpt = false;
                if ((((AEConst)this).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_INTEGER
                        || ((AEConst)this).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_DOUBLE)
                        && ((AEConst)this).mdatumValue.getDataValue().isActuallyNegative()) {
                    bHasPosNegOpt = true;
                } else if (((AEConst)this).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_COMPLEX)   {
                    if ((((AEConst)this).mdatumValue.getReal().isActuallyZero() == false && ((AEConst)this).mdatumValue.getImage().isActuallyZero() == false)
                            || ((AEConst)this).mdatumValue.getReal().isActuallyNegative()
                            || ((AEConst)this).mdatumValue.getImage().isActuallyNegative())   {
                        // means + or - operator is used. Note that when output 0 + 3*i  the output string is 3i
                        bHasPosNegOpt = true;
                    }
                }
                if (bHasPosNegOpt)  {
                    if ((enumAET.getValue() > ABSTRACTEXPRTYPES.ABSTRACTEXPR_BOPT_POSNEG.getValue()
                                && enumAET.getValue() <= ABSTRACTEXPRTYPES.ABSTRACTEXPR_INDEX.getValue())
                            || (enumAET.getValue() == ABSTRACTEXPRTYPES.ABSTRACTEXPR_BOPT_POSNEG.getValue() && nLeftOrRight <= 0))    {
                        return true;
                    }
                }
                if (((AEConst)this).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR
                        && enumAET.getValue() <= ABSTRACTEXPRTYPES.ABSTRACTEXPR_INDEX.getValue()
                        && ((AEConst)this).mdatumValue.getAExpr().menumAEType.getValue() <= enumAET.getValue()) {
                    return true;    // need brackets anyway.
                }
                return false;
            default: // ABSTRACTEXPR_DATAREFVALUE: // no matter what operator on left or right, I do not need () coz I have [] already.
                return false;
        }
    }
    
	@Override
	public int compareAExpr(AbstractExpr aexpr) throws SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		if (menumAEType.getValue() < aexpr.menumAEType.getValue())	{
			return 1;
		} else if (menumAEType.getValue() > aexpr.menumAEType.getValue())	{
			return -1;
		} else	{
			int[] nDim1 = mdatumValue.recalcDataArraySize();
			int[] nDim2 = ((AEConst)aexpr).mdatumValue.recalcDataArraySize();
			if (nDim1.length > nDim2.length)	{
				return 1;
			} else if (nDim1.length < nDim2.length)	{
				return -1;
			} else	{
				int idx;
				for (idx = 0; idx < nDim1.length; idx ++)	{
					if (nDim1[idx] > nDim2[idx])	{
						return 1;
					} else if (nDim1[idx] < nDim2[idx])	{
						return -1;
					}
				}

				// dimensions are exactly the same.
				int nAExpr1DataType = 0, nAExpr2DataType = 0;
                if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_REF_FUNC)	{
					nAExpr1DataType = 3;
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR)	{
					nAExpr1DataType = 2;
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_STRING)	{
					nAExpr1DataType = 1;
				} else	{
					nAExpr1DataType = 0;
				}
                if (((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_REF_FUNC)	{
					nAExpr2DataType = 3;
				} else if (((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR)	{
					nAExpr2DataType = 2;
				} else if (((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_STRING)	{
					nAExpr2DataType = 1;
				} else	{
					nAExpr2DataType = 0;
				}
				if (nAExpr1DataType < nAExpr2DataType)	{
					return 1;
				} else if (nAExpr1DataType > nAExpr2DataType)	{
					return -1;
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR)	{
					// aexpr.mdatumValue.GetDataType() is also DATATYPES.DATUM_ABSTRACT_EXPR
					return mdatumValue.getAExpr().compareAExpr(((AEConst)aexpr).mdatumValue.getAExpr());
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_REF_FUNC)	{
					// aexpr.mdatumValue.GetDataType() is also DATATYPES.DATUM_REF_FUNC
					return mdatumValue.getFunctionName().compareTo(((AEConst)aexpr).mdatumValue.getFunctionName());
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_STRING)	{
					// aexpr.mdatumValue.GetDataType() is also DATATYPES.DATUM_REF_FUNC
					return mdatumValue.getStringValue().compareTo(((AEConst)aexpr).mdatumValue.getStringValue());
				} else if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_REF_DATA)	{
                    BaseData.DataClass datumPopulated1 = new BaseData.DataClass();
                    datumPopulated1.copyTypeValueDeep(mdatumValue);
                    datumPopulated1.populateDataArray(nDim1, false);
                    BaseData.DataClass datumPopulated2 = new BaseData.DataClass();
                    datumPopulated2.copyTypeValueDeep(((AEConst)aexpr).mdatumValue);
                    datumPopulated2.populateDataArray(nDim2, false);
                    for (int idx1 = 0; idx1 < datumPopulated1.getDataListSize(); idx1 ++)   {
                        AEConst aeChild1 = new AEConst(datumPopulated1.getDataList()[idx1]);
                        AEConst aeChild2 = new AEConst(datumPopulated2.getDataList()[idx1]);
                        int nComparedResult = aeChild1.compareAExpr(aeChild2);
                        if (nComparedResult != 0)   {
                            return nComparedResult;
                        }
                    }
                    return 0;
                } else if ((mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_BOOLEAN
                        || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_INTEGER
                        || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_DOUBLE
                        || mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_COMPLEX)
                        && (((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_BOOLEAN
                        || ((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_INTEGER
                        || ((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_DOUBLE
                        || ((AEConst)aexpr).mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_COMPLEX))  {
					// the two values are real or image values.
                    MFPNumeric mfpNum1 = mdatumValue.getReal();
                    MFPNumeric mfpNum2 = ((AEConst)aexpr).mdatumValue.getReal();
                    double dComparedResult = mfpNum2.compareTo(mfpNum1);
                    if (dComparedResult == 0)   {
                        mfpNum1 = mdatumValue.getImage();
                        mfpNum2 = ((AEConst)aexpr).mdatumValue.getImage();
                        dComparedResult = mfpNum2.compareTo(mfpNum1);
                    }
                    return (int)dComparedResult;
				} else  {
                    return 0;   // NULL or invalid
                }
			}
		}
	}
	
	// identify if it is very, very close to 0 or zero array. Assume the expression has been simplified most
	@Override
	public boolean isNegligible() throws SMErrProcessor.JSmartMathErrException, ErrProcessor.JFCALCExpErrException {
		validateAbstractExpr();
		return mdatumValue.isZeros(false);	// do not look on NULL as zero here.
	}
	
	// output the string based expression of any abstract expression type.
	@Override
	public String output()	throws ErrProcessor.JFCALCExpErrException, SMErrProcessor.JSmartMathErrException {
		validateAbstractExpr();
		String strOutput = mdatumValue.output();
		return strOutput;
	}

    @Override
    public AbstractExpr convertAEVar2AExprDatum(LinkedList<String> listVars, boolean bNotConvertVar, LinkedList<String> listCvtedVars) throws SMErrProcessor.JSmartMathErrException {
        return this;
    }

    @Override
    public AbstractExpr convertAExprDatum2AExpr() throws SMErrProcessor.JSmartMathErrException {
        if (mdatumValue.getDataType() == BaseData.DATATYPES.DATUM_ABSTRACT_EXPR) {
            return mdatumValue.getAExpr();
        } else {
            return this;
        }
    }

    @Override
    public int getVarAppearanceCnt(String strVarName) {
        return 0;   // datum aexpr is not considered.
    }
}
