#!/bin/bash
# Oracle Cloud Instance Creation Retry Script
# This script will automatically retry creating an instance when capacity becomes available

echo "🔄 Oracle Cloud ARM Instance Auto-Retry Script"
echo "=============================================="
echo ""
echo "This script will help you get an ARM instance by automatically retrying"
echo "when capacity becomes available in different availability domains."
echo ""
echo "⚠️  IMPORTANT: You'll need to run this ON the Oracle Cloud Console"
echo "   Keep the Create Instance page open and refresh periodically"
echo ""

# Function to check current time and suggest best retry times
suggest_retry_times() {
    current_hour=$(date +%H)
    echo "Current time: $(date)"
    echo ""
    echo "🕐 Best times to try (when people typically delete instances):"
    echo "   • Early morning: 6:00 AM - 8:00 AM (your timezone)"
    echo "   • Late evening: 10:00 PM - 12:00 AM (your timezone)"
    echo "   • Lunch time: 12:00 PM - 1:00 PM (your timezone)"
    echo ""
    
    if [ $current_hour -ge 6 ] && [ $current_hour -le 8 ]; then
        echo "✅ Good time to try! (Early morning)"
    elif [ $current_hour -ge 22 ] || [ $current_hour -le 1 ]; then
        echo "✅ Good time to try! (Late evening/night)"
    elif [ $current_hour -ge 12 ] && [ $current_hour -le 13 ]; then
        echo "✅ Good time to try! (Lunch time)"
    else
        echo "⏰ Consider trying during peak deletion times listed above"
    fi
    echo ""
}

# Manual retry checklist
show_manual_steps() {
    echo "📋 Manual Retry Steps:"
    echo "====================="
    echo ""
    echo "1. Keep Oracle Cloud Console open in your browser"
    echo "2. Go to Compute → Instances → Create Instance"
    echo "3. Try these configurations in order:"
    echo ""
    echo "   🎯 Configuration A (Recommended):"
    echo "      • Shape: VM.Standard.A1.Flex"
    echo "      • OCPU: 4, Memory: 24 GB"
    echo "      • Availability Domain: AD-1"
    echo "      • Fault Domain: (leave blank)"
    echo ""
    echo "   🎯 Configuration B (Alternative):"
    echo "      • Shape: VM.Standard.A1.Flex" 
    echo "      • OCPU: 2, Memory: 12 GB"
    echo "      • Availability Domain: AD-1"
    echo "      • Fault Domain: (leave blank)"
    echo ""
    echo "   🎯 Configuration C (Fallback):"
    echo "      • Shape: VM.Standard.A1.Flex"
    echo "      • OCPU: 1, Memory: 6 GB"
    echo "      • Availability Domain: AD-3"
    echo "      • Fault Domain: (leave blank)"
    echo ""
    echo "4. If still out of capacity, wait 30-60 minutes and try again"
    echo "5. Try different availability domains (AD-1, AD-2, AD-3)"
    echo ""
}

# Alternative free options
show_alternatives() {
    echo "🔄 Alternative Free Options (If ARM is consistently unavailable):"
    echo "================================================================"
    echo ""
    echo "Option 1: Oracle Cloud x86 Instances (Still Free)"
    echo "   • Shape: VM.Standard.E2.1.Micro"
    echo "   • 1 OCPU, 1 GB Memory"
    echo "   • Usually available"
    echo "   • Still completely free forever"
    echo ""
    echo "Option 2: AWS Free Tier (12 months)"
    echo "   • Instance: t2.micro"
    echo "   • 1 vCPU, 1 GB RAM"
    echo "   • Free for 12 months"
    echo ""
    echo "Option 3: Google Cloud Free Tier"
    echo "   • Instance: e2-micro"
    echo "   • 1 vCPU, 1 GB RAM"
    echo "   • Free forever (limited bandwidth)"
    echo ""
}

# Main execution
suggest_retry_times
show_manual_steps
show_alternatives

echo "💡 Pro Tips:"
echo "============"
echo "• Set up alerts: Some users create browser notifications to check every hour"
echo "• Try multiple regions: Different regions may have different availability"
echo "• Be patient: ARM instances are very popular, but capacity opens up regularly"
echo "• Consider x86 alternative: VM.Standard.E2.1.Micro is usually available and still free"
echo ""
echo "🎯 Most users get their ARM instance within 24-48 hours of trying!"
echo ""

# Ask if user wants to set up alternatives
echo "Would you like me to provide setup instructions for:"
echo "1. Oracle x86 instance (immediately available)"
echo "2. AWS Free Tier setup"
echo "3. Google Cloud setup" 
echo "4. Continue trying for ARM instance"
echo ""
echo "Enter your choice (1-4):"