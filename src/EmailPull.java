if(line.contains("@") && (line.contains(".com") || line.contains(".net")))
{
	if(line.contains(" "))
	{
		if(line.contains(".com"))
			email = line.substring(line.indexOf(" "), line.indexOf(".com")+4);
		else if(line.contains(".net"))
			email = line.substring(line.indexOf(" "), line.indexOf(".net")+4);
		else if(line.contains(".ie"))
			email = line.substring(line.indexOf(" "), line.indexOf(".ie")+3);
		email = (email.substring(email.lastIndexOf(" "), email.length()).trim());
		if(email.contains("@") && (email.contains(".com") || email.contains(".net") || email.contains(".ie")))
			emailArray.add(email);
	}
	else
	{
		if(line.contains(".com"))
			email = line.substring(0, line.indexOf(".com")+4);
		else if(line.contains(".net"))
			email = line.substring(0, line.indexOf(".net")+4);
		else if(line.contains(".ie"))
			email = line.substring(line.indexOf(" "), line.indexOf(".ie")+3);
		if(email.contains("@") && (email.contains(".com") || email.contains(".net") || email.contains(".ie")))
			emailArray.add(email);
	}
