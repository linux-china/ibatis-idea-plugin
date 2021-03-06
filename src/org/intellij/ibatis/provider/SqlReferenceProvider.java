package org.intellij.ibatis.provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.IncorrectOperationException;
import org.intellij.ibatis.IbatisManager;
import org.intellij.ibatis.dom.sqlMap.Sql;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * sql reference provider
 */
public class SqlReferenceProvider extends BaseReferenceProvider {
    @NotNull public PsiReference[] getReferencesByElement(PsiElement psiElement) {
        final XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) psiElement;
        XmlAttributeValuePsiReference psiReference = new XmlAttributeValuePsiReference(xmlAttributeValue) {
            public boolean isSoft() {
                return false;
            }

            @Nullable public PsiElement resolve() {
//                String sqlId = getCanonicalText();
                String sqlId = getReferenceId(getElement());
                Map<String, Sql> sqlList = IbatisManager.getInstance().getAllSql(getElement());
                Sql sql = sqlList.get(sqlId);
                return sql == null ? null : sql.getXmlTag();
            }

            public Object[] getVariants() {
                Map<String, Sql> sqlList = IbatisManager.getInstance().getAllSql(getElement());
                List<String> variants = new ArrayList<String>();
                variants.addAll(sqlList.keySet());
                return variants.toArray();
            }

            /**
             * handler rename rename
             * @param newElementName     new element name
             * @return empty element
             * @throws IncorrectOperationException    exception
             */
            @Override public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
                XmlTag tag = PsiTreeUtil.getParentOfType(getElement(), XmlTag.class);
                if (tag != null) {
                    XmlAttribute attribute = (XmlAttribute) getElement().getParent();
                    tag.setAttribute(attribute.getName(), newElementName);
                }
                return null;
            }
        };
        return new PsiReference[]{psiReference};
    }

}