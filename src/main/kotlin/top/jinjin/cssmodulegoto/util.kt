package top.jinjin.cssmodulegoto

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.nio.file.Paths

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
                val resolvedPath = Paths.get(currentDir.path).resolve(path)
                val vfs = VfsUtil.findFileByIoFile(resolvedPath.toFile(), true)
                if (vfs != null) {
                    return vfs
                }
                return null
            }
            path.startsWith("@/") -> {
                val srcDir = psiFile.project.baseDir.findChild("src") ?: return null
                val rel = path.removePrefix("@/")
                val resolvedPath = Paths.get(currentDir.path).resolve(rel)
                val vfs = VfsUtil.findFileByIoFile(resolvedPath.toFile(), true)
                if (vfs != null) {
                    return vfs
                }
                return null
            }
            else -> null
        }
    }
}