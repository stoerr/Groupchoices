<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
  xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="language" content="en" />
<meta name="author" content="Various" />
<meta name="robots" content="all, index, follow" />
<meta name="allow-search" content="yes" />
<meta name="content-language" content="en" />
<meta http-equiv="content-style-type" content="text/css" />
<meta http-equiv="content-script-type" content="text/javascript" />
<meta name="revisit-after" content="31 days" />
<meta name="robots" content="index, nofollow" />
<!-- <link href="../css/styles.css" rel="stylesheet" type="text/css" /> -->
<title><%=request.getAttribute("title")%></title>
</head>
<body>
    <h1><%=request.getAttribute("title")%></h1>
    <p><%=request.getAttribute("description")%></p>
    <%=request.getAttribute("mainarea")%>
</body>
</html>
