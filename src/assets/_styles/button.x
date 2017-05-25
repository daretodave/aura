*background-brush = {
	style    = S(FILL);
	gradient = G(NONE);
	fill     = C(#E5E5E5);
	other    = C(0,0,0);
};

*text-brush = {
	fill = C(#424242);
};

~standard = {
	style   = S(OUTLINE);
	stroke  = F(2F);
	outline = C(100,100,100,255);
};

~hover(~standard) = {
	outline = C(200,200,200,255);
};

~focused(~hover) = {
	outline = C(150,150,150,255);
};

