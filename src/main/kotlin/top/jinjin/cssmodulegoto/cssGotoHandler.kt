package top.jinjin.cssmodulegoto

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.lang.ecmascript6.completion.ES6ImportExportDefaultCompletionProvider
import com.intellij.lang.ecmascript6.psi.ES6ImportDeclaration
import com.intellij.lang.ecmascript6.psi.ES6ImportSpecifier
import com.intellij.lang.ecmascript6.psi.ES6ImportedExportedDefaultBinding
import com.intellij.lang.javascript.psi.JSIndexedPropertyAccessExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentOfTypes
import com.intellij.psi.xml.XmlAttribute
import top.jinjin.cssmodulegoto.util.navigateToCssClass
import top.jinjin.cssmodulegoto.util.resolveCssFile

class cssGotoHandler: GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor
    ): Array<out PsiElement?>? {
        if (sourceElement == null) return null

        // 判断是不是 className
        val jsxAttribute = PsiTreeUtil.getParentOfType(sourceElement, XmlAttribute::class.java, false)
        if ( jsxAttribute?.name != "className") {
            return null
        }

        // 获取到 style 的 vfs，一般而言都是 import styleName from './index.less|css|scss' 等
        PsiTreeUtil.getParentOfType(sourceElement, JSIndexedPropertyAccessExpression::class.java, false)?.let {ref ->
            val imports = PsiTreeUtil.findChildrenOfType(
                ref.containingFile,
                ES6ImportDeclaration::class.java
            )

//            val  styleName = ref.qualifier?.text

            val styleName = ref.qualifier?.text
            val index = ref.indexExpression as? JSLiteralExpression
            val className = index?.stringValue ?: return null


            for (imp in imports) {

                if (imp.importedBindings[0].name == styleName) {
                    val importPath = imp.fromClause?.referenceText ?: return null
                    resolveCssFile(ref.containingFile, importPath)?.let { vfs ->
                        val navigateToCssClass = navigateToCssClass(ref.project, vfs, className)
                        if ( navigateToCssClass != null) {
                            return arrayOf( navigateToCssClass)
                        }
                    }

                    return null
                }
            }

            return null

        }
        return null
    }
}