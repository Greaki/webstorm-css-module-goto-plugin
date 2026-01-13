package top.jinjin.cssmodulegoto

import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.css.CssClass
import com.intellij.psi.css.CssRuleset
import com.intellij.psi.css.CssSelector
import com.intellij.psi.util.PsiTreeUtil
import java.nio.file.Paths

object util {
    // 找到 style 文件
    fun resolveCssFile(
        psiFile: PsiFile,
        importPath: String
    ): VirtualFile? {

        val currentFile = psiFile.virtualFile ?: return null
        val currentDir = currentFile.parent ?: return null

        val path = importPath.trim('\'', '"')

        return when {
            path.startsWith(".") -> {
                val resolvedPath = Paths.get(currentDir.path).resolve(path)
                VfsUtil.findFileByIoFile(resolvedPath.toFile(), true)
            }
            path.startsWith("@/") -> {
                val projectBasePath = psiFile.project.basePath ?: return null
                val projectBaseDir = VfsUtil.findFile(Paths.get(projectBasePath), true) ?: return null
                val srcDir = projectBaseDir.findChild("src") ?: return null
                val rel = path.removePrefix("@/")
                val resolvedPath = Paths.get(srcDir.path).resolve(rel)
                VfsUtil.findFileByIoFile(resolvedPath.toFile(), true)
            }
            else -> null
        }
    }

    fun findCssClass(
        project: Project,
        cssFile: VirtualFile,
        target: String
    ): CssSelector? {
        val psiFile = PsiManager.getInstance(project).findFile(cssFile) ?: return null

        val classSelectors = PsiTreeUtil.findChildrenOfType(
            psiFile,
            CssSelector::class.java
        )

        for (selector in classSelectors) {
            val resolved = resolve(selector) ?: continue
            if (resolved == target) {
                return selector
            }
        }
        return null
    }

    fun matchesNestedClass(cssClass: CssClass, target: String): Boolean {
        val name = cssClass.name ?: return false
        if (!name.startsWith("&")) return false

        val parent = PsiTreeUtil.getParentOfType(cssClass, CssClass::class.java)
            ?: return false

        val resolved = parent.name + name.removePrefix("&")
        return resolved == target
    }


    fun resolveSelectorText(selector: CssSelector): String? {
        var text = selector.text.trim()

        if (!text.contains("&")) {
            return text.removePrefix(".")
        }

        var current = selector
        var resolved = text

        while (resolved.contains("&")) {
            val parentRuleset = PsiTreeUtil.getParentOfType(
                current,
                CssRuleset::class.java
            )?.parent ?: return null

            val parentSelector = PsiTreeUtil.findChildOfType(
                parentRuleset,
                CssSelector::class.java
            ) ?: return null

            resolved = resolved.replace(
                "&",
                parentSelector.text.removePrefix(".")
            )

            current = parentSelector
        }

        return resolved.removePrefix(".")
    }

    /**
     * 从当前 selector 出发，递归向上解析 &，
     * 最终得到完整 class 名（不带 .）
     */
    fun resolve(selector: CssSelector): String? {
        return resolveInternal(selector)?.removePrefix(".")
    }

    private fun resolveInternal(selector: CssSelector): String? {
        val text = selector.text.trim()

        // 如果已经是普通类名，直接返回
        if (!text.contains("&")) {
            return text
        }

        // 当前 ruleset（注意：这是 &-test 自己那一层）
        val currentRuleset = PsiTreeUtil.getParentOfType(
            selector,
            CssRuleset::class.java
        ) ?: return null

        // 父 ruleset（& 的真实语义来源）
        val parentRuleset = PsiTreeUtil.getParentOfType(
            currentRuleset.parent,
            CssRuleset::class.java
        ) ?: return null

        val parentSelector = PsiTreeUtil.findChildOfType(
            parentRuleset,
            CssSelector::class.java
        ) ?: return null

        val parentResolved = resolveInternal(parentSelector)
            ?: return null

        // 用父 selector 替换 &
        return text.replace("&", parentResolved)
    }



}