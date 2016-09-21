# Gemini Web 2.2.7 NPE

Demonstration of `NullPointerException` in Gemini Web 2.2.7 when adding a bundle
fragment to a web application bundle. The problem arises when the fragment bundle
introduces a class in a package that doesn't exist in the host bundle. For example,
if in the host bundle there is a servlet in package `test`:

```java
package test;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello")
public class HostHello extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<html>Hello, world!</html>");
		writer.flush();
	}
}
```

And then in the fragment bundle a servlet in package `test.frag`:

```java
package test.frag;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/hello-frag")
public class FragHello extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<html>Hello, fragment!</html>");
		writer.flush();
	}
}
```

Then on startup Gemini Web will throw a `NullPointerException`:

```
Caused by: java.lang.NullPointerException
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleEntry.isDirectory(BundleEntry.java:231)
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleDirContext.entryToResult(BundleDirContext.java:118)
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleDirContext.entryToResult(BundleDirContext.java:113)
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleDirContext.doList(BundleDirContext.java:106)
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleDirContext.doSafeList(BundleDirContext.java:89)
	at org.eclipse.gemini.web.tomcat.internal.loading.BundleDirContext.doListBindings(BundleDirContext.java:59)
	at org.apache.naming.resources.BaseDirContext.list(BaseDirContext.java:707)
	at org.apache.naming.resources.DirContextURLConnection.list(DirContextURLConnection.java:432)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsJndi(ContextConfig.java:2013)
	at org.apache.catalina.startup.ContextConfig.processAnnotationsUrl(ContextConfig.java:1933)
	at org.apache.catalina.startup.ContextConfig.webConfig(ContextConfig.java:1311)
	at org.apache.catalina.startup.ContextConfig.configureStart(ContextConfig.java:889)
	at org.apache.catalina.startup.ContextConfig.lifecycleEvent(ContextConfig.java:386)
	at org.apache.catalina.util.LifecycleSupport.fireLifecycleEvent(LifecycleSupport.java:117)
	at org.apache.catalina.util.LifecycleBase.fireLifecycleEvent(LifecycleBase.java:90)
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:5416)
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:150)
	...
```

Looking in `BundleDirContext:118`, a `url` property is `null` for the `BundleEntry`
describing the `test.frag` package directory (described as `BundleEntry
[bundle=gemini-web-npe-host_1.0.0 [177],path=WEB-INF/classes/test/frag/]`).

The `NPE` does *not* occur if the `FragHello` servlet is moved into the `test`
package.

This repository contains two Eclipse PDE projects that expect Gemini Web to be
provided by the PDE target runtime. Launch the runtime to see the NPE thrown during
startup. I have [submitted bug 501935 to Eclipse](https://bugs.eclipse.org/bugs/show_bug.cgi?id=501935).
