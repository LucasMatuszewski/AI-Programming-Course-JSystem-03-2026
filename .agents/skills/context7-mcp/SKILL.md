---
name: context7-mcp
description: Fetch current library, framework, and API documentation through the Context7 MCP server. Use when Codex answers questions about third-party libraries, setup or configuration, API references, version-specific behavior, migrations, or when generating code that depends on external docs.
---

Use Context7 for library and framework questions instead of relying on memory when current documentation matters.

## Workflow

1. Resolve the library ID in Context7 from the package or framework name in the user request.
2. Prefer the official or primary match. If the user mentions a version, prefer a version-specific match when Context7 offers one.
3. Query the docs with the user's full question so the result is scoped to the actual task.
4. Answer from the returned docs. Mention the version when the result makes it clear.

## Guidance

- Use this skill for libraries, frameworks, SDKs, APIs, setup, configuration, migrations, and code examples.
- Prefer Context7 over model memory for unstable details such as current APIs, config keys, and framework conventions.
- If multiple matches are plausible, choose the closest official source and state the inference briefly.
- Keep the answer focused. Summarize the relevant result instead of dumping raw docs.
- If Context7 does not have the library, fall back to the primary official documentation source.
