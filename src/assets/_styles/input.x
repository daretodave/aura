*background-brush = {
	style   = S(FILL);
	fill    = C(255,255,255,255);
};

*input-brush = {
	fill = C(0, 0, 0);
};

*input-hint = {
	fill = C(0, 0, 0, 100);
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

