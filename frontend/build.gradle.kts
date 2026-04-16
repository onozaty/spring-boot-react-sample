import groovy.json.JsonSlurper

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    nodeProjectDir = projectDir
}

// npm/yarn/nodeSetup タスクはこのプロジェクトで使用しないためタスク一覧から除外
setOf("npmInstall", "npmSetup", "nodeSetup", "yarn", "yarnSetup").forEach { taskName ->
    tasks.named(taskName) { group = null }
}

val packageJson = JsonSlurper().parse(file("package.json")) as Map<*, *>
val scripts = packageJson["scripts"] as Map<*, *>

scripts.keys.forEach { scriptName ->
    tasks.register<com.github.gradle.node.pnpm.task.PnpmTask>(scriptName.toString()) {
        group = "pnpm scripts"
        description = "pnpm run $scriptName"
        args = listOf("run", scriptName.toString())
    }
}