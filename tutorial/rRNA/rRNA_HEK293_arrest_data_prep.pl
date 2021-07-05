#3       51993719        51993720        call-2  30.49238212421733       -       7236,40,420,165 2485,14,162,40  15723,6,93,63   15283,8,95,79   del11=1187,9050;del12=448,3150;del21=388,16275;del22=389,15856;deletion_pvalue=4.32308024711503E-7;deletion_score=25.54444885457633;ins11=242,9050;ins12=85,3150;ins21=267,16275;ins22=256,15856;insertion_pvalue=1.731470506627364E-4;insertion_score=14.102135983181142       *       A
#12      130876172       130876173       call-2  35.6761674625277        +       627,3,345,15    769,6,364,8     1408,9,790,22   1422,2,58,5     1365,3,50,7     del11=35,1025;del12=33,1180;del13=71,2300;del21=25,1512;del22=29,1454;deletion_pvalue=0.0033974387918858096;deletion_score=8.580704596482974;ins11=75,1025;ins12=72,1180;ins13=143,2300;ins21=74,1512;ins22=66,1454;insertion_pvalue=0.005741401170259897;insertion_score=7.629726394723548     *       A
  
while(<>)
{
	chomp;
	my @tmp = split(/\t+/,$_);
	my $offset=$#tmp-15;
	#print $offset,"\n";
	#if($#tmp==19){}
	#elsif($#tmp==20){print "HO";}
	#exit(0);
	my ($contig, $ref_position, $sample, $callScore, $base, $site, $strand) = ($tmp[0],$tmp[1]."_".$tmp[2],$tmp[6],$tmp[11],$tmp[3],$tmp[8], $tmp[12]);

	my $arrestScore = 0;
	if(/arrest\_score\=([\d\.]+)/)
	{ $arrestScore=$1;}

	
	$site=$site-$tmp[1]+1 if($strand eq '+');
        $site=$tmp[2]-$site if($strand eq '-');
							  
	printf("%s\t%s\t%s\t%s\t%.5f\t%s\t%d\t%s\n",$contig.":".$ref_position.":".$strand, $contig, $ref_position, $sample, $arrestScore, $base, $site, $strand);
}

#strand !##
