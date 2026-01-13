package top.jinjin.cssmodulegoto

import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.css.CssClass
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
                val srcDir = psiFile.project.baseDir.findChild("src") ?: return null
                val rel = path.removePrefix("@/")
                val resolvedPath = Paths.get(srcDir.path).resolve(rel)
                VfsUtil.findFileByIoFile(resolvedPath.toFile(), true)
            }
            else -> null
        }
    }

    // 跳转到 css 文件指定类名，如果没有找到类名，直接返回 null
    fun navigateToCssClass(
        project: Project,
        cssFile: VirtualFile,
        className: String
    ): CssClass? {
        val psiFile = PsiManager.getInstance(project).findFile(cssFile) ?: return null

        val cssClass = findCssClass(psiFile, className) ?: return null

        return cssClass
    }


    fun findCssClass(
        psiFile: PsiFile,
        target: String
    ): CssClass? {

        val classes = PsiTreeUtil.findChildrenOfType(
            psiFile,
            CssClass::class.java
        )

        for (cls in classes) {
            if (cls.name == target) return cls

            if (matchesNestedClass(cls, target)) {
                return cls
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



}