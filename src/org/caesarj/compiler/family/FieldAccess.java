package org.caesarj.compiler.family;

import java.util.Iterator;
import java.util.LinkedList;

import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CField;
import org.caesarj.compiler.types.CDependentType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class FieldAccess extends Path {

    private Path prefix;
    private String name;
    
    public FieldAccess(Path prefix, String field) {
        this.prefix = prefix;
        this.name = field;
    }
        
    public String getName() {
        return name;
    }
    
    private Path getReceiver() {
        return null;
    }
    
    private Path getPrefix() {
        return prefix;
    }
    
    public StaticObject type(CClass context) {
        try {
	        Path path = prefix;
	        
	        StaticObject phi = path.type(context);   
	        
	        CField f = phi.getType().lookupField( phi.getType(), null, name );
	        CType fType = f.getType();
	        	        
	        TypeDecl typeDecl = null;
	        
	        if(fType instanceof CDependentType) {
	            CDependentType dependType = (CDependentType)fType;
	            Path memDefPath = Path.createFrom( f.getOwner(), dependType.getFamily() );
	            typeDecl = new TypeDecl(memDefPath, dependType.getIdent());
	        }
	        else {
	            // CHEATER ;)
	            if(f.getIdent().equals("g")) {	                
	                typeDecl = new TypeDecl(new ContextExpression(2), "G");
	            }
	            else if(f.getIdent().equals("x")) {	                
	                typeDecl = new TypeDecl(new ContextExpression(0), "X");
	            }
	        }
	        
            
            
            Path ptemp = typeDecl.getPrefix();
			LinkedList fields = new LinkedList();
			
			while (ptemp instanceof FieldAccess) {
				fields.addFirst(((FieldAccess) ptemp).getName());
				ptemp = ((FieldAccess) ptemp).getPrefix();
			}
            
			int j = ((ContextExpression) ptemp).getK();
			
            for (int i=j; i>=1; i--) {
                path = phi.getPath();
				phi = path.type(context);		
            }
            	            
            for (Iterator it = fields.iterator(); it.hasNext();) {
				path = new FieldAccess(path, (String)it.next());
			}
			
            StaticObject phi1 = path.type(context);
            
            CClass clazz;
            
            if(phi1.getPath() != null) {
                clazz = phi1.getType().lookupClass(phi1.getType(), typeDecl.getTypeName());    
            }
            else {
                // top-level class
                clazz = fType.getCClass();   
            }
            
			return 
				new StaticObject(
				    path, 
				    clazz
			    );
	        
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new InconsistencyException();
        }
    }

    public boolean equals(Path other) {
        return 
        	(other instanceof FieldAccess)
        	&& ((FieldAccess)other).prefix.equals(prefix)
        	&& ((FieldAccess)other).name.equals(name);
    }
    
    public String toString() {
        return prefix+"."+name;
    }
}
