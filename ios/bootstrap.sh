#!/usr/bin/env bash
set -o pipefail

function print_in_center() {
    let z=44-${#1}
    let y=z/2
    let x=z-y

	printf "                   |%*s%s%*s|\n" $x "" "$1" $y ""
}

function padding() {
	echo "                   |                                            |"
}

function ending() {
 	echo "                   |                                            |"
    echo '                   "--------------------------------------------"'

}

function install_templates() {
	cd Xcode\ Templates/
	sh templates.sh install

	# check if script succeds
	if [ $? -eq 0 ]; then
		print_in_center "Xcode Templates installed"
	else
		print_in_center "Unable to install Xcode Templates"
	fi

	cd ..
}

function install_git_hooks() {
	cd Git\ hooks/
	sh hooks.sh install 2> /dev/null

	# check if script succeds
	if [ $? -eq 0 ] || [ $? -eq 1 ]; then
		print_in_center "git hooks installed"
	else
		print_in_center "Unable to install git hooks"
	fi

	cd ..
}

tput setaf 190;
printf "                                                                 \n"
printf "                                                                 \n"
printf "                               ..&KMY5777^7777YYfQ&..'           \n"
printf "                           ..#Y7  ...&&MMMMM&&&...  7YM&.'       \n"
printf "                        .N#5 ..MMMMWYY!!!!!!!?YWWMMMa.. ?Wa.'    \n"
printf "                      .M5 ..MMM5!  ..&KfYYY5VVa&.. ?YMMN. .YN.'  \n"
printf "                    .MD  &MMY! ..#5^            .7Ya. ?WMN. .4p' \n"
printf "                   .#! .MMY  .M5                    JWp .WMh. Jh.\n"
printf "..................M  .MM$ .J8........................MF  4MN  4b'\n"
printf "                                                           MM.  M\n"
printf "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM#   M\n"
printf "                                                             ...#\n"
printf "7777777777777777777YYYYYYYYYY777777777777777777777777777777777!!'\n"
printf "                   Jb  JMMp  YN.'                                \n"
printf "                    TN. .WMM.. ?WN..            ...#WWWMMM#WWWM$'\n"
printf "                     .4a. .YMMN..  ?TWMff&JfffWY7!  .&MM@!  JM!' \n"
printf "                       .Ya.  ?YWMMN&....    ....&&MMMY^! ..@5'   \n"
printf "                          ?WN..  .!YWWMMMMMMMMWWY!!  ...MY'      \n"
printf "                              ?TWQ&.....    ......&fW7!'         \n"
printf "                                    .!!!7777!!!!'                \n"
printf "                                                                 \n"
printf "                                                                 \n"
tput setaf 6;
echo "                   .____________________________________________."
echo "                   |                                            |"

cd Utils/
install_templates
padding
install_git_hooks
ending

cd ..

exit 0
