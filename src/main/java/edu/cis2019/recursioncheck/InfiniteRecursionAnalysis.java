package edu.cis2019.recursioncheck;

import edu.cis2019.recursioncheck.Common.ErrorMessage;
import edu.cis2019.recursioncheck.Common.Utils;
import soot.*;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfiniteRecursionAnalysis extends BodyTransformer
{
    private List<Unit> baseCases;
    private List<UnitValuePair> recursiveCases;
    private List<UnitValuePair> invokeExprs;

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        System.out.println("analysis started");

        baseCases = new ArrayList<Unit>();
        recursiveCases = new ArrayList<UnitValuePair>();
        invokeExprs = new ArrayList<UnitValuePair>();

        SootMethod sourceMethod = body.getMethod();
        System.out.println();
        System.out.println("Analyzing method: " + sourceMethod.getName());

        invokeExprs = getAllInvokeExpr(body, sourceMethod);
        if (isRecursive(body.getUnits(), sourceMethod)) {
            if (!hasBaseCase(body.getUnits(), sourceMethod)) {
                Utils.reportWarning(sourceMethod, ErrorMessage.NO_BASE_CASE);
            } else if (sourceMethod.getParameterCount() != 0) {
                List<Unit> unchangedRecursiveCases = getParameterNeverChanged(body);
                if (!unchangedRecursiveCases.isEmpty()) {
                    for (Unit unchangedRecursiveCase : unchangedRecursiveCases) {
                        Utils.reportWarning(unchangedRecursiveCase, ErrorMessage.PARAMETERS_UNCHANGED);
                    }
                }
            } else if (sourceMethod.getParameterCount() == 0) {
                for (UnitValuePair recursiveCase : recursiveCases) {
                    Utils.reportWarning(recursiveCase.unit, ErrorMessage.PARAMETERS_UNCHANGED);
                }
            }
        }
        if (!invokeExprs.isEmpty()) {
            List<UnitValuePair> callList = new ArrayList<UnitValuePair>();
            for (UnitValuePair pair : invokeExprs) {
                callList = findCallList(callList, pair.unit, pair.value, sourceMethod);
            }
            for (UnitValuePair pair : callList) {
                if (pair.value.getMethod().equals(sourceMethod))
                    Utils.reportWarning(pair.unit, ErrorMessage.MUTUAL_RECURSIVE_WARNING);
            }
        }
    }

    private boolean isRecursive(PatchingChain<Unit> units, SootMethod sourceMethod) {
        for (Unit unit : units) {
            System.out.println(unit);
            List<ValueBox> valueBoxes = unit.getUseBoxes();
            for (ValueBox valueBox : valueBoxes) {
                Value value = valueBox.getValue();
                if (value instanceof InvokeExpr) {
                    SootMethod invokedMethod = ((InvokeExpr) value).getMethod();
                    if (invokedMethod == sourceMethod) {
                        recursiveCases.add(new UnitValuePair(unit, (InvokeExpr) value));
                    }
                }
            }
        }
        return !recursiveCases.isEmpty();
    }

    private List<UnitValuePair> getAllInvokeExpr(Body body, SootMethod sourceMethod) {
        List<UnitValuePair> result = new ArrayList<UnitValuePair>();
        for (Unit unit : body.getUnits()) {
            for (ValueBox valueBox : unit.getUseBoxes()) {
                Value value = valueBox.getValue();
                if (value instanceof InvokeExpr && !(value instanceof SpecialInvokeExpr)) {
                    if (((InvokeExpr) value).getMethod() != sourceMethod)
                        result.add(new UnitValuePair(unit, (InvokeExpr) value));
                }
            }
        }
        return result;
    }

    private boolean hasBaseCase(PatchingChain<Unit> units, SootMethod sourceMethod) {
        for (Unit unit : units) {
            if (unit instanceof ReturnStmt) {
                Value returnVar = ((ReturnStmt) unit).getOp();
                if (returnVar instanceof Constant || returnVar instanceof Ref) {
                    baseCases.add(unit);
                } else if (returnVar instanceof InvokeExpr) {
                    SootMethod invokedMethod = ((InvokeExpr) returnVar).getMethod();
                    if (sourceMethod != invokedMethod)
                        baseCases.add(unit);
                }
            }
        }
        return !baseCases.isEmpty();
    }

    private List<Value> getUnchangedLocals(Body body) {
        List<Value> assignedWithParam = new ArrayList<Value>();
        for (Unit unit : body.getUnits()) {
            if (unit instanceof DefinitionStmt) {
                Value left = ((DefinitionStmt) unit).getLeftOp();
                Value right = ((DefinitionStmt) unit).getRightOp();
                if (right instanceof ParameterRef)
                    assignedWithParam.add(left);
                else assignedWithParam.remove(left);
            }
        }
        return assignedWithParam;
    }

    private List<Unit> getParameterNeverChanged(Body body) {
        List<Value> unchangedLocals = getUnchangedLocals(body);
        List<Unit> result = new ArrayList<Unit>();
        for (UnitValuePair pair : recursiveCases) {
            List<Value> params = pair.value.getArgs();
            boolean changed = false;
            for (Value value : params) {
                if (!(value instanceof Constant || value instanceof ParameterRef
                        || unchangedLocals.contains(value)))
                    changed = true;
            }
            if (!changed)
                result.add(pair.unit);
        }
        return result;
    }

    private List<UnitValuePair> findCallList(List<UnitValuePair> callList,
                                             Unit unitInput, InvokeExpr invokeExpr, SootMethod thisMethod) {
        List<Value> needAnalyze = new ArrayList<Value>();
        SootMethod method = invokeExpr.getMethod();

        callList.add(new UnitValuePair(unitInput, invokeExpr));
        try {
            for (Unit unit : method.retrieveActiveBody().getUnits()) {
                for (ValueBox valueBox : unit.getUseBoxes()) {
                    Value value = valueBox.getValue();
                    if (value instanceof InvokeExpr) {
                        InvokeExpr call = (InvokeExpr) value;
                        if (!(callList.contains(new UnitValuePair(unitInput, call)))) {
                            callList.add(new UnitValuePair(unitInput, call));
                            if (!(call.getMethod().equals(thisMethod)))
                                needAnalyze.add(call);
                        }
                    }
                }
            }
        } catch (java.lang.RuntimeException e) {

        }
        for (Value value : needAnalyze) {
            List<UnitValuePair> tempCallList = findCallList(callList, unitInput, (InvokeExpr) value, thisMethod);
            for (UnitValuePair pair : tempCallList)
                callList.add(pair);
        }
        return callList;
    }

    private class UnitValuePair {
        public final Unit unit;
        public final InvokeExpr value;

        public UnitValuePair(Unit unit, InvokeExpr value) {
            this.unit = unit;
            this.value = value;
        }
    }

    private static InfiniteRecursionAnalysis theInstance = new InfiniteRecursionAnalysis();
    public static InfiniteRecursionAnalysis instance() { return theInstance; }
}