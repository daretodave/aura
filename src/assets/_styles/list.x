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

