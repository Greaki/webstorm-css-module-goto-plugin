package top.jinjin.cssmodulegoto

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

object util {
    fun resolveCssFile(
        psiFile: PsiFile,
        importPath: String
    ): VirtualFile? {

        val currentFile = psiFile.virtualFile ?: return null
        val currentDir = currentFile.parent ?: return null

        val path = importPath.trim('\'', '"')

        return when {
            path.startsWith(".") -> {
                VfsUtil.findRelativeFile(
                    currentDir,
                    *path.split("/").toTypedArray()
                )
            }
            path.startsWith("@/") -> {
                val srcDir = psiFile.project.baseDir.findChild("src") ?: return null
                val rel = path.removePrefix("@/")
                VfsUtil.findRelativeFile(
                    srcDir,
                    *rel.split("/").toTypedArray()
                )
            }
            else -> null
        }
    }
}