# Generate all outputs for the project.
NAME=cognition

default : compile

clean : 
	$(RM) $(NAME).aux
	$(RM) $(NAME).bbl
	$(RM) $(NAME).blg
	$(RM) $(NAME).dvi
	$(RM) $(NAME).log
	$(RM) $(NAME).out
	$(RM) $(NAME).pdf
	$(RM) $(NAME).ps
	$(RM) $(NAME).tpt

compile :
	pdflatex		$(NAME).tex	# Make aux that bibtex need.
	pdflatex		$(NAME).tex	# Make aux that bibtex need.
	#bibtex		$(NAME).aux	# Go through all references.	
	#latex		$(NAME).tex	# Make dvi.
	#latex		$(NAME).tex	# Complete references.
	#$(RM)		$(NAME).aux	# Delete because the current aux was not generate with hyperref and pdflatex will complain.
	#pdflatex 	$(NAME).tex	# Generate PDF.
	#pdflatex 	$(NAME).tex	# Complete references.
