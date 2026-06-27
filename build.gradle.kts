plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.maven.publish) apply false
}

subprojects {
    val injectWasmPreloads by tasks.registering {
        description = "Injects preload links for generated Wasm distribution artifacts."

        doLast {
            val distDir = layout.buildDirectory.dir("dist/wasmJs/productionExecutable").get().asFile
            val indexFile = distDir.resolve("index.html")
            if (!indexFile.isFile) return@doLast

            val scriptPreloads = distDir
                .listFiles { file -> file.isFile && file.extension == "js" }
                .orEmpty()
                .sortedBy { it.name }
                .map { """  <link rel="preload" href="${it.name}" as="script">""" }

            val wasmPreloads = distDir
                .listFiles { file -> file.isFile && file.extension == "wasm" }
                .orEmpty()
                .sortedBy { it.name }
                .map { """  <link rel="preload" href="${it.name}" as="fetch" type="application/wasm" crossorigin>""" }

            val preloadBlock = (scriptPreloads + wasmPreloads).joinToString(
                separator = "\n",
                prefix = "  <!-- wasm-preloads:start -->\n",
                postfix = "\n  <!-- wasm-preloads:end -->",
            )

            val existingPreloadBlock = Regex(
                pattern = """\n?  <!-- wasm-preloads:start -->.*?  <!-- wasm-preloads:end -->\n?""",
                options = setOf(RegexOption.DOT_MATCHES_ALL),
            )
            val indexHtml = indexFile.readText().replace(existingPreloadBlock, "\n")
            val updatedIndexHtml = indexHtml.replaceFirst("</title>", "</title>\n$preloadBlock")
            indexFile.writeText(updatedIndexHtml)
        }
    }

    tasks.matching { it.name == "wasmJsBrowserDistribution" }.configureEach {
        finalizedBy(injectWasmPreloads)
    }
}
