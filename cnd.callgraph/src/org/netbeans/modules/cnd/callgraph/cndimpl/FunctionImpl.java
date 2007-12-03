package org.netbeans.modules.cnd.callgraph.cndimpl;

import java.awt.Image;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmFunctionDefinition;
import org.netbeans.modules.cnd.api.model.CsmMember;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.callgraph.api.Function;
import org.netbeans.modules.cnd.modelutil.CsmImageLoader;
import org.netbeans.modules.cnd.modelutil.CsmUtilities;
import org.openide.util.NbBundle;

public class FunctionImpl implements Function {

    private CsmFunction function;
    private String htmlDisplayName = "";

    public FunctionImpl(CsmFunction function) {
        super();
        this.function = function;
    }

    public CsmFunction getDeclaration() {
        if (CsmKindUtilities.isFunctionDefinition(function)) {
            CsmFunction f = ((CsmFunctionDefinition) function).getDeclaration();
            if (f != null) {
                return f;
            }
        }
        return function;
    }

    public CsmFunction getDefinition() {
        if (CsmKindUtilities.isFunctionDeclaration(function)) {
            CsmFunction f = function.getDefinition();
            if (f != null) {
                return f;
            }
        }
        return function;
    }

    public String getName() {
        return function.getName();
    }

    public String getHtmlDisplayName() {
        if (htmlDisplayName.length() == 0) {
            htmlDisplayName = createHtmlDisplayName();
        }
        return htmlDisplayName;
    }

    private String createHtmlDisplayName() {
        String displayName = function.getName().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        try {
            CsmFunction f = (CsmFunction) getDeclaration();
            if (CsmKindUtilities.isClassMember(f)) {
                CsmClass cls = ((CsmMember) f).getContainingClass();
                if (cls != null && cls.getName().length() > 0) {
                    String name = cls.getName().replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    String in = NbBundle.getMessage(CallImpl.class, "LBL_inClass");
                    //NOI18N
                    return displayName + "<font color=\'!controlShadow\'>  " + in + " " + name;
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return displayName;
    }

    public String getDescription() {
        return function.getSignature();
    }

    public Image getIcon() {
        try {
            return CsmImageLoader.getImage((CsmFunction) getDefinition());
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void open() {
        CsmUtilities.openSource(getDefinition());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionImpl) {
            return getDeclaration().equals(((FunctionImpl) obj).getDeclaration());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getDeclaration().hashCode();
    }
}
