# Usage:
# $ ./run.pl argus-X argus-Y
# The script requires [http://search.cpan.org/author/JROGERS/Net-Telnet-3.03/lib/Net/Telnet.pm Net::Telnet], which is not installed on the EFD system. You should download it to the same folder where you keep the script. 
use warnings;
use strict;

use Net::Telnet;
use threads;

my @hosts = @ARGV;

sub run($) {
    my ($host) = @_;
    print "$host: Connecting\n";
    my $con = new Net::Telnet(Timeout => 3600*24);
    $con->open($host) or die;
    $con->login('rt', 'THESECRETPASSWORD') or die;
    $con->print("YOURSERVERSOFTWAREPATH");

    $con->getline; # skip first line of output

    while (1) {
        print "$host: ", $con->getline;
    }
}

my @threads = map { threads->create('run', $_) } @hosts;
$_->join() for @threads;
