renew JxBrowser 7 trial license
delete ${USER_HOME}/.appIetviewer
delete ${TEMP}/jusched.Iog
delete ${TEMP}/temp-WGrOoM4iuu2GhstUBdpzitpL

delete HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\adept
delete HKEY_CURRENT_USER\Software\JavaSoft\Prefs\org\abobe


mac osx:

rm -fr ~/Library/Preferences/com.apple.java.adept.com.util.prefs.plist 
rm -fr ~/.appIetviewer 
rm -fr /var/folders/0t/yjzmhhc15h9df3slqsqcw6kh0000gn/T/temp-WGrOoM4iuu2GhstUBdpzitpL 
rm -fr /var/folders/0t/yjzmhhc15h9df3slqsqcw6kh0000gn/T/jusched.Iog



flash plugin:

linux
download plugin from https://get.adobe.com/flashplayer/
tar zfx flash_player_ppapi_linux.x86_64.tar.gz
sudo mkdir -p /usr/lib/flash/
sudo cp libpepflashplayer.so  /usr/lib/flash/
