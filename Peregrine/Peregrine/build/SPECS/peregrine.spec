# This is a spec file for Peregrine proxy

%define _topdir	 	/home/tom/git/peregrineserver/build/
%define name		peregrine
%define release		1
%define version 	1
%define buildroot	%{_topdir}/%{name}-%{version}
%define _prefix		/usr/local/bin/peregrine

BuildRoot:	%	{buildroot}
Summary: 		peregrine web accelerator
License: 		GPL
Name: 			%{name}
Version: 		%{version}
Release: 		%{release}
Prefix: 		%{_prefix}
Group: 			Application/Proxy

%description
The reverse proxy accelerates static web content

%prep
#rm -rf $RPM_BUILD_ROOT

%build

%install
mkdir -p ${RPM_BUILD_ROOT}%{_prefix}
cp ./*.jar $RPM_BUILD_ROOT%{_prefix}
cp ./peregrine.sh $RPM_BUILD_ROOT%{_prefix}
mkdir -p ${RPM_BUILD_ROOT}/opt/peregrine/cache

%files
#%defattr(-,root,root)
%{_prefix}/

%post
jar -xvf %{_prefix}/PeregrineServer-1.0-SNAPSHOT-jar-with-dependencies.jar peregrine.properties
mv /peregrine.properties %{_prefix}
zip -d PeregrineServer-1.0-SNAPSHOT-jar-with-dependencies.jar peregrine.properties
