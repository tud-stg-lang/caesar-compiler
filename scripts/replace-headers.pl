#!/usr/bin/perl
# This script replaces the source file headers 

$files = `find ../src/* -name *.java`;

@files = split(/\n/, $files);

$header = `cat header.txt`;

foreach $f (@files) {	
	open (IN, $f);
	open (OUT, ">$f.tmp");	
	
	$_ = "";
	while($line = <IN>)  {
		$_ .= $line;
	
	}
	close(IN);

	/package org\.caesarj\..+;(\n|\r\n)/;

	print OUT $header;	
	print OUT $&;
	print OUT $';
		
	close(OUT);
	`mv $f.tmp $f`;
}
