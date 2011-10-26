# Use this script to upload your server to the cameras
#
# usage:
# $ ./ftp.pl argus-x argus-y argus-z

use warnings;
use strict;

use Net::FTP;

my @cameras = @ARGV;
my $groupid = 'groupXX';
my $filename = 'Server';

sub upload($) {
    my ($host) = @_;
    print "Uploading to `$host'\n";

    my $ftp = Net::FTP->new($host, Timeout => 7, Passive => 1) or die;
    $ftp->login('rt', 'DETHEMLIGALÖSENORDET') or die;
    $ftp->mkdir("/var/tmp/$groupid", 1);
    $ftp->delete("/var/tmp/$groupid/$filename");
    $ftp->cwd("/var/tmp/$groupid") or die;
    $ftp->binary() or die;
    $ftp->put("../build/$filename") or die;
    $ftp->quot('SITE', 'CHMOD', '711', "/var/tmp/$groupid/$filename") or die;
    $ftp->quit();
}

upload($_) for (@cameras);
