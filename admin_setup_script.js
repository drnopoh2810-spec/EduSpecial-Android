// Admin Setup Script for EduSpecial
// Run this script in Firebase Console > Firestore > Run Query

// 1. Create Admin User Profile
// First, the user must register normally through the app with:
// Email: mahmoudnabihsaleh@gmail.com
// Password: ZXCVzxcv

// 2. After registration, run this script to upgrade to admin
const adminEmail = "mahmoudnabihsaleh@gmail.com";

// Step 1: Find the user by email
db.collection("users")
  .where("email", "==", adminEmail)
  .get()
  .then((querySnapshot) => {
    if (querySnapshot.empty) {
      console.log("❌ User not found. Please register first through the app.");
      return;
    }
    
    const userDoc = querySnapshot.docs[0];
    const userId = userDoc.id;
    
    console.log(`✅ Found user: ${userId}`);
    
    // Step 2: Update user to admin with verification
    return db.collection("users").doc(userId).update({
      role: "ADMIN",
      accountStatus: "ACTIVE",
      emailVerified: true,
      isAdmin: true, // For admin dashboard access
      updatedAt: Date.now(),
      updatedBy: "system",
      adminAssignedAt: Date.now(),
      adminAssignedBy: "system_setup"
    });
  })
  .then(() => {
    console.log("✅ User upgraded to ADMIN successfully!");
    
    // Step 3: Log the admin assignment
    return db.collection("role_assignments").add({
      assignerId: "system",
      targetUserId: userId,
      oldRole: "USER",
      newRole: "ADMIN",
      reason: "Initial admin setup",
      timestamp: Date.now()
    });
  })
  .then(() => {
    console.log("✅ Admin assignment logged!");
    
    // Step 4: Log security event
    return db.collection("security_logs").add({
      userId: userId,
      event: "ROLE_CHANGE",
      timestamp: Date.now(),
      details: {
        oldRole: "USER",
        newRole: "ADMIN",
        assignedBy: "system",
        reason: "Initial admin setup"
      },
      success: true
    });
  })
  .then(() => {
    console.log("✅ Security event logged!");
    console.log("🎉 Admin setup completed successfully!");
    console.log(`📧 Admin email: ${adminEmail}`);
    console.log("🔑 Admin can now access:");
    console.log("   - Admin Dashboard (web)");
    console.log("   - Content Moderation");
    console.log("   - User Management");
    console.log("   - System Analytics");
  })
  .catch((error) => {
    console.error("❌ Error setting up admin:", error);
  });

// Alternative: Manual Firestore Document Update
// If the script doesn't work, manually update the user document:
/*
1. Go to Firebase Console > Firestore Database
2. Find the user document in 'users' collection with email: mahmoudnabihsaleh@gmail.com
3. Edit the document and add/update these fields:
   {
     "role": "ADMIN",
     "accountStatus": "ACTIVE", 
     "emailVerified": true,
     "isAdmin": true,
     "updatedAt": [current timestamp],
     "adminAssignedAt": [current timestamp]
   }
*/