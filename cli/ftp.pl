#!/usr/bin/perl
# Use this script to upload your server to the cameras
#
# usage:
# $ ./ftp.pl argus-x argus-y argus-z

use warnings;
use strict;

use Net::FTP;

my @cameras = @ARGV;
my $groupid = 'group06';
my $filename = 'CameraServer';

sub upload($) {
    my ($host) = @_;
    my ($pw) = $ENV{'PW'};
    #print "PW is $pw";
    print "Uploading to `$host'...";

    my $ftp = Net::FTP->new($host, Timeout => 7, Passive => 1) or die;
    $ftp->login('rt', $pw) or die;
    $ftp->mkdir("/var/tmp/$groupid", 1);
    $ftp->delete("/var/tmp/$groupid/$filename");
    $ftp->cwd("/var/tmp/$groupid") or die;
    $ftp->binary() or die;
    $ftp->put("c/build/build/$filename") or die;
    $ftp->quot('SITE', 'CHMOD', '711', "/var/tmp/$groupid/$filename") or die;
    $ftp->quit();
    print "done!\n";
}

upload($_) for (@cameras);
