package org.caesarj.compiler.family;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;


public class FjLinkedFamily extends FjFamily
{
	private FjFamily otherFamily;
	private FjFamily family;
	
	public FjLinkedFamily(FjFamily family, FjFamily otherFamily)
	{
		super(family.getType(), family.getFamilyAccessor());
		
		this.family = family;
		this.otherFamily = otherFamily;
	}

	public String getIdentification() 
	{
		return family.getIdentification() + ";" 
			+ otherFamily.getIdentification();
	}
	
	public CReferenceType getInnerType()
	{
		return otherFamily.getInnerType();
	}

	public boolean isOuterThis()
	{
		return family.isOuterThis();
	}

	public boolean isParameter()
	{
		return family.isParameter();
	}

	public boolean isThis()
	{
		return family.isThis();
	}

	public void setInnerType(CTypeContext context, CReferenceType innerType)
		throws UnpositionedError
	{
		otherFamily.setInnerType(context, innerType);
	}

	public FjFamily first()
	{
		return this;
	}
	public CReferenceType getType()
	{
		return otherFamily.getType();
	}

}
